package arena_vibe_volei.api.Repository;


import Model.Quadra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuadraRepository extends JpaRepository<Quadra, Long> {
}