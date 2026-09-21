package arena_vibe_volei.api.Repository;

import Model.Reserva;
import arena_vibe_volei.api.Enum.StatusReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByQuadraIdOrderByInicioReserva(Long quadraId);

    Optional<Reserva> findFirstByQuadraIdAndStatusOrderByInicioReserva(
            Long quadraId, StatusReserva status);

    List<Reserva> findByQuadraIdAndStatusIn(Long quadraId, Collection<StatusReserva> status);
}
