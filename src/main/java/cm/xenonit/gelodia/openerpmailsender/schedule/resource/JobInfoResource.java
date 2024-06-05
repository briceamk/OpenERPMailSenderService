package cm.xenonit.gelodia.openerpmailsender.schedule.resource;

import cm.xenonit.gelodia.openerpmailsender.common.dto.PageInfoDto;
import cm.xenonit.gelodia.openerpmailsender.common.enums.SortDirection;
import cm.xenonit.gelodia.openerpmailsender.schedule.domain.JobInfo;
import cm.xenonit.gelodia.openerpmailsender.schedule.generated.resource.JobInfosApi;
import cm.xenonit.gelodia.openerpmailsender.schedule.generated.resource.dto.ApiSuccessResponseDto;
import cm.xenonit.gelodia.openerpmailsender.schedule.generated.resource.dto.CreateJobInfoRequestDto;
import cm.xenonit.gelodia.openerpmailsender.schedule.resource.dto.JobInfoDto;
import cm.xenonit.gelodia.openerpmailsender.schedule.resource.mapper.JobInfoMapper;
import cm.xenonit.gelodia.openerpmailsender.schedule.service.JobInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import static java.time.LocalDateTime.now;
import static java.util.Map.of;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

/**
 * @author bamk
 * @version 1.0
 * @since 17/02/2024
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class JobInfoResource implements JobInfosApi {

    private final JobInfoService jobInfoService;
    private final JobInfoMapper jobInfoMapper;

    @Override
    @PreAuthorize("hasAuthority('create:job_info')")
    public ResponseEntity<ApiSuccessResponseDto> createJobInfo(CreateJobInfoRequestDto jobInfoRequestDto) {
        JobInfoDto jobInfoDto = jobInfoMapper.fromJobInfo(
                jobInfoService.createJobInfo(jobInfoMapper.fromCreateJobInfoRequestDto(jobInfoRequestDto)));
        return ResponseEntity
                .status(CREATED)
                .body(
                        new ApiSuccessResponseDto()
                                .timestamp(now().toString())
                                .code(CREATED.value())
                                .status(CREATED.getReasonPhrase())
                                .message("Job created successfully")
                                .success(true)
                                .data(of("data", jobInfoDto))
                );
    }

    @Override
    @PreAuthorize("hasAuthority('update:delete:job_info')")
    public ResponseEntity<ApiSuccessResponseDto> deleteJobById(String jobInfoId) {
        return ResponseEntity
                .status(OK)
                .body(
                        new ApiSuccessResponseDto()
                                .timestamp(now().toString())
                                .code(OK.value())
                                .status(OK.getReasonPhrase())
                                .message("Job trigger deleted successfully")
                                .success(true)
                                .data(of("data", jobInfoMapper.fromJobInfo(jobInfoService.deleteJob(jobInfoId))))
                );
    }

    @Override
    @PreAuthorize("hasAuthority('read:job_info')")
    public ResponseEntity<ApiSuccessResponseDto> fetchJobInfoByKeyword(Integer page, Integer size, String direction, String attribute, String keyword) {
        Page<JobInfo> jobInfoPage = jobInfoService.fetchJobInfoByKeyword(page, size, SortDirection.valueOf(direction), attribute, keyword);
        return ResponseEntity
                .status(OK)
                .body(
                        new ApiSuccessResponseDto()
                                .timestamp(now().toString())
                                .code(OK.value())
                                .status(OK.getReasonPhrase())
                                .message("Job found successfully")
                                .success(true)
                                .data(
                                        of(
                                                "data", jobInfoMapper.fromJobInfoPage(jobInfoPage),
                                                "pageInfo", PageInfoDto.builder()
                                                        .first(jobInfoPage.isFirst())
                                                        .last(jobInfoPage.isLast())
                                                        .totalElements(jobInfoPage.getTotalElements())
                                                        .totalPages(jobInfoPage.getTotalPages())
                                                        .pageSize(size)
                                                        .build()
                                        )
                                )
                );
    }

    @Override
    @PreAuthorize("hasAuthority('read:job_info')")
    public ResponseEntity<ApiSuccessResponseDto> fetchJobInfos(Integer page, Integer size, String direction, String attribute) {
        Page<JobInfo> jobInfoPage = jobInfoService.fetchJobInfos(page, size, SortDirection.valueOf(direction), attribute);
        return ResponseEntity
                .status(OK)
                .body(
                        new ApiSuccessResponseDto()
                                .timestamp(now().toString())
                                .code(OK.value())
                                .status(OK.getReasonPhrase())
                                .message("Job found successfully")
                                .success(true)
                                .data(
                                        of(
                                        "data", jobInfoMapper.fromJobInfoPage(jobInfoPage),
                                        "pageInfo", PageInfoDto.builder()
                                                .first(jobInfoPage.isFirst())
                                                .last(jobInfoPage.isLast())
                                                .totalElements(jobInfoPage.getTotalElements())
                                                .totalPages(jobInfoPage.getTotalPages())
                                                        .pageSize(size)
                                                .build()
                                        )
                                )
                );
    }

    @Override
    @PreAuthorize("hasAuthority('read:job_info')")
    public ResponseEntity<ApiSuccessResponseDto> findJobInfoById(String jobInfoId) {
        return ResponseEntity
                .status(OK)
                .body(
                        new ApiSuccessResponseDto()
                                .timestamp(now().toString())
                                .code(OK.value())
                                .status(OK.getReasonPhrase())
                                .message("Job found successfully")
                                .success(true)
                                .data(of("data", jobInfoMapper.fromJobInfo(jobInfoService.findJobById(jobInfoId))))
                );
    }

    @Override
    @PreAuthorize("hasAuthority('update:re_schedule:job_info')")
    public ResponseEntity<ApiSuccessResponseDto> reScheduleJobById(String jobInfoId) {
        return ResponseEntity
                .status(OK)
                .body(
                        new ApiSuccessResponseDto()
                                .timestamp(now().toString())
                                .code(OK.value())
                                .status(OK.getReasonPhrase())
                                .message("Job trigger re-scheduled successfully")
                                .success(true)
                                .data(of("data", jobInfoMapper.fromJobInfo(jobInfoService.reScheduleJob(jobInfoId))))
                );
    }

    @Override
    @PreAuthorize("hasAuthority('update:resume:job_info')")
    public ResponseEntity<ApiSuccessResponseDto> resumeJobById(String jobInfoId) {
        return ResponseEntity
                .status(OK)
                .body(
                        new ApiSuccessResponseDto()
                                .timestamp(now().toString())
                                .code(OK.value())
                                .status(OK.getReasonPhrase())
                                .message("Job trigger resumed successfully")
                                .success(true)
                                .data(of("data", jobInfoMapper.fromJobInfo(jobInfoService.reScheduleJob(jobInfoId))))
                );
    }

    @Override
    @PreAuthorize("hasAuthority('update:schedule:job_info')")
    public ResponseEntity<ApiSuccessResponseDto> scheduleJobById(String jobInfoId) {
        return ResponseEntity
                .status(OK)
                .body(
                        new ApiSuccessResponseDto()
                                .timestamp(now().toString())
                                .code(OK.value())
                                .status(OK.getReasonPhrase())
                                .message("Job trigger scheduled successfully")
                                .success(true)
                                .data(of("data", jobInfoMapper.fromJobInfo(jobInfoService.scheduledJob(jobInfoId))))
                );
    }

    @Override
    @PreAuthorize("hasAuthority('update:start:job_info')")
    public ResponseEntity<ApiSuccessResponseDto> startJobById(String jobInfoId) {
        return ResponseEntity
                .status(OK)
                .body(
                        new ApiSuccessResponseDto()
                                .timestamp(now().toString())
                                .code(OK.value())
                                .status(OK.getReasonPhrase())
                                .message("Job trigger started successfully")
                                .success(true)
                                .data(of("data", jobInfoMapper.fromJobInfo(jobInfoService.startJob(jobInfoId))))
                );
    }

    @Override
    @PreAuthorize("hasAuthority('update:un_schedule:job_info')")
    public ResponseEntity<ApiSuccessResponseDto> unScheduleJobById(String jobInfoId) {
        return ResponseEntity
                .status(OK)
                .body(
                        new ApiSuccessResponseDto()
                                .timestamp(now().toString())
                                .code(OK.value())
                                .status(OK.getReasonPhrase())
                                .message("Job trigger un-scheduled successfully")
                                .success(true)
                                .data(of("data", jobInfoMapper.fromJobInfo(jobInfoService.unScheduledJob(jobInfoId))))
                );
    }
}
