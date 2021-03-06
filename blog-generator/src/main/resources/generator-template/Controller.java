package ${basePath}${moduleName}.controller${subDir};

import io.swagger.annotations.*;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import ${basePath}${moduleName}.entity.domain${subDir}.${name};
import ${basePath}${moduleName}.entity.pojo${subDir}.${name}PoJo;
import ${basePath}${moduleName}.entity.request${subDir}.${name}Request;
import ${basePath}${moduleName}.entity.response${subDir}.${name}Response;
import ${basePath}.system.service.common.CommonService;
import ${basePath}${moduleName}.service${subDir}.${name}Service;
import io.github.ramerf.wind.core.entity.response.Rs;
import ${basePath}.system.util.TextUtil;
import ${basePath}${moduleName}.validator${subDir}.${name}Validator;
import io.github.ramerf.wind.core.entity.response.Rs;
import io.github.ramerf.wind.core.helper.ControllerHelper;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Slf4j
@Controller("${alia}c")
@PreAuthorize("hasAnyAuthority('global:read','${alia}:read')")
@RequestMapping( "${subDirRequest}/${alia}")
@Api(tags = "${description}接口")
@SuppressWarnings("UnusedDeclaration")
public class ${name}Controller {
  @Resource private ${name}Service service;
  @Resource private ${name}Validator validator;

  @InitBinder
  void initBinder(WebDataBinder binder) {
    binder.addValidators(validator);
  }

  @GetMapping("/index")
  @ApiOperation("${description}页面")
  public String index() {
    return "${alia}/index";
  }

  @GetMapping("/page")
  @ResponseBody
  @ApiOperation("获取${description}列表")
  public ResponseEntity<Rs<PageImpl<${name}Response>>> page(
      @ApiParam("页号,从1开始,当page=size=-1时,表示不分页")
          @RequestParam(value = "page", required = false, defaultValue = "1")
          String pageStr,
      @RequestParam(value = "size", required = false, defaultValue = "10") String sizeStr,
      @ApiParam("查询条件") @RequestParam(value = "criteria", required = false) String criteria) {
    final int[] pageAndSize = TextUtil.validFixPageAndSize(pageStr, sizeStr);
    return ControllerHelper.page(
        service.page(criteria, pageAndSize[0], pageAndSize[1]), ${name}Response::of);
  }

  @GetMapping
  @ApiOperation("添加${description}页面")
  public String create() {
    return "${alia}/edit";
  }

  @PostMapping
  @ResponseBody
  @PreAuthorize("hasAnyAuthority('global:create','${alia}:create')")
  @ApiOperation("添加${description}")
  public ResponseEntity<Rs<Object>> create(
      @Valid ${name}Request ${alia}Request, @ApiIgnore BindingResult bindingResult) {
    log.info(" ${name}Controller.create : [{}]", ${alia}Request);
    return ControllerHelper.create(
        service, ${name}.class, ${alia}Request, bindingResult);
  }

  @GetMapping("/{id}")
  @ApiOperation("更新${description}页面")
  public String update(@PathVariable("id") String idStr, @ApiIgnore Map<String, Object> map) {
    return ControllerHelper.update(
        service,
        ${name}PoJo.class,
        idStr,
        "${alia}/edit",
        map, "${alia}");
  }

  @PutMapping("/{id}")
  @ResponseBody
  @PreAuthorize("hasAnyAuthority('global:write','${alia}:write')")
  @ApiOperation("更新${description}")
  public ResponseEntity<Rs<Object>> update(
      @PathVariable("id") String idStr,
      @Valid ${name}Request ${alia}Request,
      @ApiIgnore BindingResult bindingResult) {
    log.info(" ${name}Controller.update : [{}]", ${alia}Request);
    final long id = TextUtil.validLong(idStr, -1);
    if (id < 1) {
      return Rs.wrongFormat("id");
    }
    ${alia}Request.setId(id);
    return ControllerHelper.update(
        service, ${name}.class, ${alia}Request, idStr, bindingResult);
  }

  @DeleteMapping("/{id}")
  @ResponseBody
  @PreAuthorize("hasAnyAuthority('global:delete','${alia}:delete')")
  @ApiOperation("删除${description}")
  public ResponseEntity<Rs<Object>> delete(@PathVariable("id") long id) {
    return ControllerHelper.delete(service, id);
  }

  @DeleteMapping("/deleteBatch")
  @ResponseBody
  @PreAuthorize("hasAnyAuthority('global:delete','${alia}:delete')")
  @ApiOperation("删除${description}批量")
  public ResponseEntity<Rs<Object>> deleteByIds(@RequestParam("ids") List<Long> ids) {
    return ControllerHelper.deleteByIds(service, ids);
  }
}
