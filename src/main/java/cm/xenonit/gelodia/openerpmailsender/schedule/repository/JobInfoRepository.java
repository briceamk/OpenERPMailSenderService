package cm.xenonit.gelodia.openerpmailsender.schedule.repository;

import cm.xenonit.gelodia.openerpmailsender.schedule.domain.JobInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * @author bamk
 * @version 1.0
 * @since 17/02/2024
 */
public interface JobInfoRepository extends JpaRepository<JobInfo, String> {

    Boolean existsByJobNameIgnoreCase(String jobName);
    Boolean existsByJobClassIgnoreCase(String jobClass);
    Optional<JobInfo> findByJobName(String jobName);
    @Query("SELECT ji FROM JobInfo ji WHERE LOWER((CONCAT(ji.jobName, ' ', ji.jobGroup, ' ',  ji.jobClass, ' ', ji.state, ' ', CONCAT(ji.useAsCronJob,'')))) LIKE %:keyword%")
    Page<JobInfo> findByKeyword(@Param("keyword") String lowerCase, Pageable pageable);
}
