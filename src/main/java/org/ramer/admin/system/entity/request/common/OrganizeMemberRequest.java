package org.ramer.admin.system.entity.request.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.ramer.admin.system.entity.request.AbstractEntityRequest;

/**
 * 组织成员.
 *
 * @author ramer
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ApiModel("组织成员")
public class OrganizeMemberRequest extends AbstractEntityRequest {
  @ApiModelProperty(example = "1", value = "成员", required = true)
  private Long memberId;

  @ApiModelProperty(example = "1", value = "组织", required = true)
  private Long organizeId;

  @ApiModelProperty(example = "1", value = "岗位", required = true)
  private Long postId;
}
