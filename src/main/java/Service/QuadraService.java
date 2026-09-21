package Service;

import Model.Quadra;
import Model.Reserva;
import arena_vibe_volei.api.Dto.DisponibilidadeDTO;
import arena_vibe_volei.api.Dto.QuadraRequestDTO;
import arena_vibe_volei.api.Dto.QuadraResponseDTO;
import arena_vibe_volei.api.Dto.QuadraCadastroDTO;
import arena_vibe_volei.api.Dto.ReservaResponseDTO;
import arena_vibe_volei.api.Enum.StatusPagamento;
import arena_vibe_volei.api.Enum.StatusQuadra;
import arena_vibe_volei.api.Enum.StatusReserva;
import arena_vibe_volei.api.Repository.QuadraRepository;
import arena_vibe_volei.api.Repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuadraService {

    private static final List<StatusReserva> STATUS_ATIVOS = Arrays.asList(
            StatusReserva.AGENDADA, StatusReserva.EM_ANDAMENTO);

    @Autowired
    private QuadraRepository quadraRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Value("${app.reserva.multiplicador-hora-extra:1.5}")
    private BigDecimal multiplicadorHoraExtra;

    @Transactional
    public List<QuadraResponseDTO> listarTodasParaPainel() {
        return quadraRepository.findAll().stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public Quadra cadastrarQuadra(QuadraCadastroDTO dto) {
        if (dto == null || dto.getNome() == null || dto.getNome().isBlank()) {
            throw new IllegalArgumentException("Informe o nome da quadra");
        }
        if (dto.getValorHora() == null || dto.getValorHora().signum() <= 0) {
            throw new IllegalArgumentException("Informe um valor de hora maior que zero");
        }

        Quadra quadra = new Quadra();
        quadra.setNome(dto.getNome().trim());
        quadra.setValorHora(dto.getValorHora());
        quadra.setStatus(StatusQuadra.LIVRE);
        return quadraRepository.save(quadra);
    }

    @Transactional
    public Quadra iniciarReserva(Long id, QuadraRequestDTO dto) {
        agendarReserva(id, dto);
        return quadraRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Quadra não encontrada"));
    }

    @Transactional
    public ReservaResponseDTO agendarReserva(Long id, QuadraRequestDTO dto) {
        validarDadosReserva(dto);

        Quadra quadra = buscarQuadra(id);
        LocalDateTime inicio = dto.getInicioReserva() != null
                ? dto.getInicioReserva()
                : LocalDateTime.now();
        LocalDateTime fim = inicio.plusMinutes(dto.getDuracaoMinutos());

        if (fim.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("A reserva não pode terminar no passado");
        }
        if (existeConflito(quadra, inicio, fim)) {
            throw new IllegalStateException("Já existe uma reserva nesse horário");
        }

        Reserva reserva = new Reserva();
        reserva.setQuadra(quadra);
        reserva.setClienteResponsavel(dto.getClienteResponsavel().trim());
        reserva.setInicioReserva(inicio);
        reserva.setFimPrevistoReserva(fim);
        reserva.setStatus(inicio.isAfter(LocalDateTime.now())
                ? StatusReserva.AGENDADA
                : StatusReserva.EM_ANDAMENTO);
        reserva.setValorBase(calcularValor(quadra.getValorHora(), dto.getDuracaoMinutos()));
        reserva.setValorTotal(reserva.getValorBase());

        Reserva salva = reservaRepository.save(reserva);
        atualizarEstadoQuadra(quadra, salva, LocalDateTime.now());
        return converterParaReservaDTO(salva);
    }

    @Transactional
    public ReservaResponseDTO cancelarReserva(Long reservaId) {
        Reserva reserva = buscarReserva(reservaId);
        if (reserva.getStatus() == StatusReserva.CONCLUIDA) {
            throw new IllegalStateException("Uma reserva concluída não pode ser cancelada");
        }
        if (reserva.getStatus() == StatusReserva.CANCELADA) {
            return converterParaReservaDTO(reserva);
        }

        reserva.setStatus(StatusReserva.CANCELADA);
        reserva.setStatusPagamento(reserva.getStatusPagamento() == StatusPagamento.PAGO
                ? StatusPagamento.ESTORNADO
                : StatusPagamento.PENDENTE);
        reserva.setCanceladaEm(LocalDateTime.now());
        Reserva salva = reservaRepository.save(reserva);

        Quadra quadra = reserva.getQuadra();
        Reserva atual = reservaRepository
                .findFirstByQuadraIdAndStatusOrderByInicioReserva(quadra.getId(), StatusReserva.EM_ANDAMENTO)
                .orElse(null);
        if (atual == null) {
            limparEstadoQuadra(quadra);
            quadraRepository.save(quadra);
        }
        return converterParaReservaDTO(salva);
    }

    @Transactional
    public ReservaResponseDTO registrarPagamento(Long reservaId) {
        Reserva reserva = buscarReserva(reservaId);
        if (reserva.getStatus() == StatusReserva.CANCELADA) {
            throw new IllegalStateException("Uma reserva cancelada não pode ser paga");
        }
        reserva.setStatusPagamento(StatusPagamento.PAGO);
        return converterParaReservaDTO(reservaRepository.save(reserva));
    }

    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> listarReservas() {
        return reservaRepository.findAll().stream()
                .sorted((a, b) -> b.getInicioReserva().compareTo(a.getInicioReserva()))
                .map(this::converterParaReservaDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public Quadra liberarQuadra(Long id) {
        Quadra quadra = buscarQuadra(id);
        Reserva reserva = reservaRepository
                .findFirstByQuadraIdAndStatusOrderByInicioReserva(id, StatusReserva.EM_ANDAMENTO)
                .orElse(null);

        if (reserva != null) {
            atualizarCobrancaHoraExtra(reserva, LocalDateTime.now());
            reserva.setStatus(StatusReserva.CONCLUIDA);
            reservaRepository.save(reserva);
        }

        limparEstadoQuadra(quadra);
        return quadraRepository.save(quadra);
    }

    @Transactional
    public List<DisponibilidadeDTO> listarDisponibilidade(
            Long id, LocalDate inicio, LocalDate fim) {
        if (inicio == null || fim == null || fim.isBefore(inicio)) {
            throw new IllegalArgumentException("Informe um período de datas válido");
        }
        if (inicio.plusDays(90).isBefore(fim)) {
            throw new IllegalArgumentException("O período máximo para consulta é de 90 dias");
        }

        Quadra quadra = buscarQuadra(id);
        List<Reserva> reservas = reservasDaQuadra(quadra);
        return inicio.datesUntil(fim.plusDays(1))
                .map(data -> {
                    LocalDateTime inicioDia = data.atStartOfDay();
                    LocalDateTime fimDia = data.plusDays(1).atStartOfDay();
                    List<ReservaResponseDTO> doDia = reservas.stream()
                            .filter(reserva -> reserva.getStatus() != StatusReserva.CANCELADA)
                            .filter(reserva -> reserva.getInicioReserva().isBefore(fimDia)
                                    && reserva.getFimPrevistoReserva().isAfter(inicioDia))
                            .map(this::converterParaReservaDTO)
                            .collect(Collectors.toList());
                    return new DisponibilidadeDTO(data, doDia.isEmpty(), doDia);
                })
                .collect(Collectors.toList());
    }

    private QuadraResponseDTO converterParaDTO(Quadra quadra) {
        LocalDateTime agora = LocalDateTime.now();
        List<Reserva> reservas = reservasDaQuadra(quadra);
        Reserva atual = reservas.stream()
                .filter(reserva -> reserva.getStatus() == StatusReserva.EM_ANDAMENTO)
                .findFirst()
                .orElse(null);

        reservas.stream()
                .filter(reserva -> reserva.getStatus() == StatusReserva.AGENDADA
                        && !reserva.getInicioReserva().isAfter(agora))
                .findFirst()
                .ifPresent(reserva -> {
                    reserva.setStatus(StatusReserva.EM_ANDAMENTO);
                    reservaRepository.save(reserva);
                    atualizarEstadoQuadra(quadra, reserva, agora);
                });

        atual = reservas.stream()
                .filter(reserva -> reserva.getStatus() == StatusReserva.EM_ANDAMENTO)
                .findFirst()
                .orElse(atual);

        QuadraResponseDTO response = new QuadraResponseDTO();
        response.setId(quadra.getId());
        response.setNome(quadra.getNome());
        response.setStatus(quadra.getStatus());
        response.setValorHora(quadra.getValorHora());
        response.setClienteResponsavel(quadra.getClienteResponsavel());
        response.setFimPrevistoReserva(quadra.getFimPrevistoReserva());
        response.setReservaAtual(atual == null ? null : converterParaReservaDTO(atual));
        response.setProximasReservas(reservas.stream()
                .filter(reserva -> reserva.getStatus() == StatusReserva.AGENDADA)
                .filter(reserva -> reserva.getInicioReserva().isAfter(agora))
                .map(this::converterParaReservaDTO)
                .collect(Collectors.toList()));
        response.setHistoricoReservas(reservas.stream()
                .map(this::converterParaReservaDTO)
                .collect(Collectors.toList()));

        if (atual != null) {
            atualizarCobrancaHoraExtra(atual, agora);
            long segundos = Duration.between(atual.getFimPrevistoReserva(), agora).getSeconds();
            if (segundos <= 0) {
                response.setSegundosRestantes(Math.abs(segundos));
                response.setSegundosUltrapassados(0);
                response.setHoraExtraAtiva(false);
            } else {
                response.setSegundosRestantes(0);
                response.setSegundosUltrapassados(segundos);
                response.setHoraExtraAtiva(atual.getMinutosExcedentes() >= 10);
                if (response.isHoraExtraAtiva()) {
                    quadra.setStatus(StatusQuadra.HORA_EXTRA);
                    response.setStatus(StatusQuadra.HORA_EXTRA);
                    quadraRepository.save(quadra);
                }
            }
        }
        response.setReservaAtual(atual == null ? null : converterParaReservaDTO(atual));
        return response;
    }

    private boolean existeConflito(Quadra quadra, LocalDateTime inicio, LocalDateTime fim) {
        return reservasDaQuadra(quadra).stream()
                .filter(reserva -> STATUS_ATIVOS.contains(reserva.getStatus()))
                .anyMatch(reserva -> reserva.getInicioReserva().isBefore(fim)
                        && reserva.getFimPrevistoReserva().isAfter(inicio));
    }

    private List<Reserva> reservasDaQuadra(Quadra quadra) {
        return reservaRepository.findByQuadraIdOrderByInicioReserva(quadra.getId());
    }

    private void atualizarEstadoQuadra(Quadra quadra, Reserva reserva, LocalDateTime agora) {
        if (reserva.getStatus() == StatusReserva.EM_ANDAMENTO
                && !reserva.getInicioReserva().isAfter(agora)) {
            quadra.setStatus(StatusQuadra.OCUPADA);
            quadra.setClienteResponsavel(reserva.getClienteResponsavel());
            quadra.setInicioReserva(reserva.getInicioReserva());
            quadra.setFimPrevistoReserva(reserva.getFimPrevistoReserva());
            quadraRepository.save(quadra);
        }
    }

    private void atualizarCobrancaHoraExtra(Reserva reserva, LocalDateTime agora) {
        if (!agora.isAfter(reserva.getFimPrevistoReserva())) {
            return;
        }
        long segundos = Duration.between(reserva.getFimPrevistoReserva(), agora).getSeconds();
        long minutos = (segundos + 59) / 60;
        reserva.setMinutosExcedentes(minutos);
        BigDecimal taxa = calcularValor(
                reserva.getQuadra().getValorHora().multiply(multiplicadorHoraExtra),
                minutos);
        reserva.setTaxaHoraExtra(taxa);
        reserva.setValorTotal(reserva.getValorBase().add(taxa));
        if (minutos >= 10 && reserva.getStatus() == StatusReserva.EM_ANDAMENTO) {
            reserva.getQuadra().setStatus(StatusQuadra.HORA_EXTRA);
        }
        reservaRepository.save(reserva);
    }

    private BigDecimal calcularValor(BigDecimal valorHora, long minutos) {
        return valorHora.multiply(BigDecimal.valueOf(minutos))
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
    }

    private void limparEstadoQuadra(Quadra quadra) {
        quadra.setStatus(StatusQuadra.LIVRE);
        quadra.setClienteResponsavel(null);
        quadra.setInicioReserva(null);
        quadra.setFimPrevistoReserva(null);
    }

    private void validarDadosReserva(QuadraRequestDTO dto) {
        if (dto == null || dto.getClienteResponsavel() == null
                || dto.getClienteResponsavel().isBlank()) {
            throw new IllegalArgumentException("Informe quem fará a reserva");
        }
        if (dto.getDuracaoMinutos() <= 0) {
            throw new IllegalArgumentException("A duração deve ser maior que zero");
        }
    }

    private Quadra buscarQuadra(Long id) {
        return quadraRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Quadra não encontrada"));
    }

    private Reserva buscarReserva(Long id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reserva não encontrada"));
    }

    private ReservaResponseDTO converterParaReservaDTO(Reserva reserva) {
        ReservaResponseDTO response = new ReservaResponseDTO();
        response.setId(reserva.getId());
        response.setQuadraId(reserva.getQuadra().getId());
        response.setQuadraNome(reserva.getQuadra().getNome());
        response.setClienteResponsavel(reserva.getClienteResponsavel());
        response.setInicioReserva(reserva.getInicioReserva());
        response.setFimPrevistoReserva(reserva.getFimPrevistoReserva());
        response.setStatus(reserva.getStatus());
        response.setStatusPagamento(reserva.getStatusPagamento());
        response.setValorBase(reserva.getValorBase());
        response.setMinutosExcedentes(reserva.getMinutosExcedentes());
        response.setTaxaHoraExtra(reserva.getTaxaHoraExtra());
        response.setValorTotal(reserva.getValorTotal());
        response.setCanceladaEm(reserva.getCanceladaEm());
        return response;
    }
}
