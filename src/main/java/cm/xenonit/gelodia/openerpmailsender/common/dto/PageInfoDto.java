package cm.xenonit.gelodia.openerpmailsender.common.dto;

import lombok.*;

/**
 * @author bamk
 * @version 1.0
 * @since 10/02/2024
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageInfoDto {

    private Boolean first;
    private Boolean last;
    private Long totalElements;
    private Integer totalPages;
    private Integer pageSize;
}
