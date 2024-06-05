package cm.xenonit.gelodia.openerpmailsender.openerp.reopository;

import cm.xenonit.gelodia.openerpmailsender.openerp.domain.Instance;
import cm.xenonit.gelodia.openerpmailsender.openerp.domain.enums.InstanceState;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * @author bamk
 * @version 1.0
 * @since 21/02/2024
 */
public interface InstanceRepository extends JpaRepository<Instance, String> {
    boolean existsByHostAndPortAndDb(String host, Integer port, String db);
    Optional<Instance> findByHostAndPortAndDb(String host, Integer port, String db);
    @Query("SELECT i FROM Instance i WHERE LOWER((CONCAT(i.host, ' ', CONCAT(i.port, ''), ' ', i.db))) LIKE %:keyword%")
    Page<Instance> findByKeyword(@Param("keyword") String lowerCase, Pageable pageable);

    List<Instance> findByState(InstanceState instanceState);
}
