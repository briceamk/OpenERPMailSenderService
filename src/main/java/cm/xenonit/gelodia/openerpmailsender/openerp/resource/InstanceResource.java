package cm.xenonit.gelodia.openerpmailsender.openerp.resource;

import cm.xenonit.gelodia.openerpmailsender.common.dto.PageInfoDto;
import cm.xenonit.gelodia.openerpmailsender.common.enums.SortDirection;
import cm.xenonit.gelodia.openerpmailsender.instance.generated.resource.InstancesApi;
import cm.xenonit.gelodia.openerpmailsender.instance.generated.resource.dto.ApiSuccessResponseDto;
import cm.xenonit.gelodia.openerpmailsender.instance.generated.resource.dto.CreateInstanceRequestDto;
import cm.xenonit.gelodia.openerpmailsender.instance.generated.resource.dto.UpdateInstanceRequestDto;
import cm.xenonit.gelodia.openerpmailsender.openerp.domain.Instance;
import cm.xenonit.gelodia.openerpmailsender.openerp.resource.mapper.InstanceMapper;
import cm.xenonit.gelodia.openerpmailsender.openerp.service.InstanceService;
import lombok.RequiredArgsConstructor;
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
 * @since 21/02/2024
 */
@RestController
@RequiredArgsConstructor
public class InstanceResource implements InstancesApi {

    private final InstanceService instanceService;
    private final InstanceMapper instanceMapper;

    @Override
    @PreAuthorize("hasAuthority('update:activate:instance')")
    public ResponseEntity<ApiSuccessResponseDto> activateInstance(String instanceId) {
        return ResponseEntity.status(OK)
                .body(
                        new ApiSuccessResponseDto()
                                .timestamp(now().toString())
                                .code(OK.value())
                                .status(OK.getReasonPhrase())
                                .message("Instance is now activated.")
                                .success(true)
                                .data(of("data", instanceMapper.fromInstance(
                                        instanceService.activateInstance(instanceId))))
                );
    }

    @Override
    @PreAuthorize("hasAuthority('create:instance')")
    public ResponseEntity<ApiSuccessResponseDto> createInstance(CreateInstanceRequestDto createInstanceRequestDto) {
        return ResponseEntity.status(CREATED)
                .body(
                        new ApiSuccessResponseDto()
                                .timestamp(now().toString())
                                .code(CREATED.value())
                                .status(CREATED.getReasonPhrase())
                                .message("Instance created successfully.")
                                .success(true)
                                .data(of("data", instanceMapper.fromInstance(
                                        instanceService.createInstance(instanceMapper.fromCreateInstanceRequestDto(createInstanceRequestDto)))))
                );
    }

    @Override
    @PreAuthorize("hasAuthority('read:instance')")
    public ResponseEntity<ApiSuccessResponseDto> fetchInstanceById(String instanceId) {
        return ResponseEntity.status(OK)
                .body(
                        new ApiSuccessResponseDto()
                                .timestamp(now().toString())
                                .code(OK.value())
                                .status(OK.getReasonPhrase())
                                .message("Instance found.")
                                .success(true)
                                .data(of("data", instanceMapper.fromInstance(
                                        instanceService.fetchInstanceById(instanceId))))
                );
    }

    @Override
    @PreAuthorize("hasAuthority('read:instance')")
    public ResponseEntity<ApiSuccessResponseDto> fetchInstanceByKeyword(Integer page, Integer size, String direction, String attribute, String keyword) {
        Page<Instance> instancePage = instanceService.fetchInstancesByKeyword(page, size, SortDirection.valueOf(direction), attribute, keyword);
        return ResponseEntity.status(OK)
                .body(
                        new ApiSuccessResponseDto()
                                .timestamp(now().toString())
                                .code(OK.value())
                                .status(OK.getReasonPhrase())
                                .message("Instance found.")
                                .success(true)
                                .data(of(
                                        "data", instanceMapper.fromInstancePage(instancePage),
                                        "pageInfo", PageInfoDto.builder()
                                                .first(instancePage.isFirst())
                                                .last(instancePage.isLast())
                                                .totalElements(instancePage.getTotalElements())
                                                .totalPages(instancePage.getTotalPages())
                                                .pageSize(size)
                                                .build()
                                ))
                );
    }

    @Override
    @PreAuthorize("hasAuthority('read:instance')")
    public ResponseEntity<ApiSuccessResponseDto> fetchInstances(Integer page, Integer size, String direction, String attribute) {
        Page<Instance> instancePage = instanceService.fetchInstances(page, size, SortDirection.valueOf(direction), attribute);
        return ResponseEntity.status(OK)
                .body(
                        new ApiSuccessResponseDto()
                                .timestamp(now().toString())
                                .code(OK.value())
                                .status(OK.getReasonPhrase())
                                .message("Instance found.")
                                .success(true)
                                .data(of(
                                        "data", instanceMapper.fromInstancePage(instancePage),
                                        "pageInfo", PageInfoDto.builder()
                                                .first(instancePage.isFirst())
                                                .last(instancePage.isLast())
                                                .totalElements(instancePage.getTotalElements())
                                                .totalPages(instancePage.getTotalPages())
                                                .pageSize(size)
                                                .build()
                                ))
                );
    }

    @Override
    @PreAuthorize("hasAuthority('update:inactive:instance')")
    public ResponseEntity<ApiSuccessResponseDto> inactiveInstance(String instanceId) {
        return ResponseEntity.status(OK)
                .body(
                        new ApiSuccessResponseDto()
                                .timestamp(now().toString())
                                .code(OK.value())
                                .status(OK.getReasonPhrase())
                                .message("Instance is now inactive.")
                                .success(true)
                                .data(of("data", instanceMapper.fromInstance(
                                        instanceService.inactiveInstance(instanceId))))
                );
    }

    @Override
    @PreAuthorize("hasAuthority('update:instance')")
    public ResponseEntity<ApiSuccessResponseDto> updateInstance(String instanceId, UpdateInstanceRequestDto instanceRequestDto) {
        return ResponseEntity.status(OK)
                .body(
                        new ApiSuccessResponseDto()
                                .timestamp(now().toString())
                                .code(OK.value())
                                .status(OK.getReasonPhrase())
                                .message("Instance updated successfully.")
                                .success(true)
                                .data(of(
                                        "data", instanceMapper.fromInstance(instanceService.updateInstance(instanceId, instanceMapper.fromUpdateInstanceRequestDto(instanceRequestDto)))
                                ))
                );
    }
}
