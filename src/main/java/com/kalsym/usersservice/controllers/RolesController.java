package com.kalsym.usersservice.controllers;

import com.kalsym.usersservice.VersionHolder;
import com.kalsym.usersservice.models.HttpReponse;
import com.kalsym.usersservice.models.daos.Authority;
import com.kalsym.usersservice.models.daos.Role;
import com.kalsym.usersservice.models.daos.RoleAuthorityIdentity;
import com.kalsym.usersservice.models.daos.RoleAuthority;
import com.kalsym.usersservice.repositories.AuthoritiesRepository;
import com.kalsym.usersservice.repositories.RoleAuthoritiesRepository;
import com.kalsym.usersservice.repositories.RolesRepository;
import com.kalsym.usersservice.utils.Logger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Sarosh
 */
@RestController()
@RequestMapping("/roles")
public class RolesController {

    @Autowired
    RolesRepository rolesRepository;

    @Autowired
    AuthoritiesRepository authoritiesRepository;

    @Autowired
    RoleAuthoritiesRepository roleAuthoritiesRepository;

    @GetMapping(path = {"/"}, name = "roles-get")
    @PreAuthorize("hasAnyAuthority('roles-get', 'all')")
    public ResponseEntity<HttpReponse> getRoles(HttpServletRequest request,
            @RequestParam(required = false) String id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String parentRoleId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        String logprefix = request.getRequestURI() + " ";
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "", "");

        Role role = new Role();
        role.setId(id);
        role.setName(name);
        role.setParentRoleId(parentRoleId);

        Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, role + "", "");
        ExampleMatcher matcher = ExampleMatcher
                .matchingAll()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<Role> example = Example.of(role, matcher);

        Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "page: " + page + " pageSize: " + pageSize, "");
        Pageable pageable = PageRequest.of(page, pageSize);

        response.setSuccessStatus(HttpStatus.OK);
        response.setData(rolesRepository.findAll(example, pageable));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping(path = {"/{id}"}, name = "roles-get-by-id")
    @PreAuthorize("hasAnyAuthority('roles-get-by-id', 'all')")
    public ResponseEntity<HttpReponse> getRoleById(HttpServletRequest request, @PathVariable String id) {
        String logprefix = request.getRequestURI() + " ";
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "", "");

        Optional<Role> optRole = rolesRepository.findById(id);

        if (!optRole.isPresent()) {
            Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "role not found", "");
            response.setErrorStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "role found", "");
        response.setSuccessStatus(HttpStatus.OK);
        response.setData(optRole.get());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping(path = {"/{id}"}, name = "roles-delete-by-id")
    @PreAuthorize("hasAnyAuthority('roles-delete-by-id', 'all')")
    public ResponseEntity<HttpReponse> deleteRoleById(HttpServletRequest request, @PathVariable String id) {
        String logprefix = request.getRequestURI() + " ";
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "", "");

        Optional<Role> optRole = rolesRepository.findById(id);

        if (!optRole.isPresent()) {
            Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "role not found", "");
            response.setErrorStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "role found", "");
        rolesRepository.delete(optRole.get());

        Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "role deleted", "");
        response.setSuccessStatus(HttpStatus.OK);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping(path = {"/{id}"}, name = "roles-put-by-id")
    @PreAuthorize("hasAnyAuthority('roles-put-by-id', 'all')")
    public ResponseEntity<HttpReponse> putRoleById(HttpServletRequest request, @PathVariable String id, @RequestBody Role body) {
        String logprefix = request.getRequestURI() + " ";
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "", "");
        Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, body.toString(), "");

        Optional<Role> optRole = rolesRepository.findById(id);

        if (!optRole.isPresent()) {
            Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "role not found", "");
            response.setErrorStatus(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "role found", "");
        Role role = optRole.get();
        List<String> errors = new ArrayList<>();

        List<Role> roles = rolesRepository.findAll();

        for (Role existingRole : roles) {
            if (!role.equals(existingRole)) {
                if (existingRole.getId().equals(body.getId())) {
                    Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "roleId already exists", "");
                    response.setErrorStatus(HttpStatus.CONFLICT);
                    errors.add("roleId already exists");
                    response.setData(errors);
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                }
                if (existingRole.getName().equals(body.getName())) {
                    Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "name already exists", "");
                    response.setErrorStatus(HttpStatus.CONFLICT);
                    errors.add("name already exists");
                    response.setData(errors);
                    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
                }
            }

        }
        role.updateRole(body);

        Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "role updated for id: " + id, "");
        response.setSuccessStatus(HttpStatus.ACCEPTED);
        response.setData(rolesRepository.save(role));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @PostMapping(name = "roles-post")
    @PreAuthorize("hasAnyAuthority('roles-post', 'all')")
    public ResponseEntity<HttpReponse> postRole(HttpServletRequest request, @Valid @RequestBody Role body) throws Exception {
        String logprefix = request.getRequestURI() + " ";
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "", "");
        Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, body.toString(), "");

        List<Role> roles = rolesRepository.findAll();
        List<String> errors = new ArrayList<>();

        for (Role existingRole : roles) {
            if (existingRole.getId().equals(body.getId())) {
                Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "roleId already exists", "");
                response.setErrorStatus(HttpStatus.CONFLICT);
                errors.add("roleId already exists");
                response.setData(errors);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
            if (existingRole.getName().equals(body.getName())) {
                Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "name already exists", "");
                response.setErrorStatus(HttpStatus.CONFLICT);
                errors.add("name already exists");
                response.setData(errors);
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
        }

        Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "role created with id: " + body.getId(), "");
        response.setSuccessStatus(HttpStatus.CREATED);
        response.setData(rolesRepository.save(body));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping(path = {"/{roleId}/authorities"}, name = "roles-get-authorities-by-roleId")
    @PreAuthorize("hasAnyAuthority('roles-get-authorities-by-roleId', 'all')")
    public ResponseEntity<HttpReponse> getRoleAuthoritiesByRoleId(HttpServletRequest request,
            @PathVariable String roleId) {
        String logprefix = request.getRequestURI() + " ";
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "", "");

        Optional<Role> optRole = rolesRepository.findById(roleId);

        if (!optRole.isPresent()) {
            Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "role not found", "");
            response.setErrorStatus(HttpStatus.NOT_FOUND, "role not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "role found", "");

        response.setSuccessStatus(HttpStatus.OK);
        response.setData(roleAuthoritiesRepository.findByRoleId(roleId));
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping(path = {"/{roleId}/authorities/{authorityId}"}, name = "roles-delete-authorities-by-id")
    @PreAuthorize("hasAnyAuthority('roles-delete-authorities-by-id', 'all')")
    public ResponseEntity<HttpReponse> deleteRoleAuthority(HttpServletRequest request,
            @PathVariable String roleId, @PathVariable String authorityId) {
        String logprefix = request.getRequestURI() + " ";
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "", "");

        RoleAuthorityIdentity roleAuthoritiyIdentity = new RoleAuthorityIdentity(roleId, authorityId);
        Optional<RoleAuthority> optRoleAuthority = roleAuthoritiesRepository.findById(roleAuthoritiyIdentity);

        if (!optRoleAuthority.isPresent()) {
            Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "role_authority not found", "");
            response.setErrorStatus(HttpStatus.NOT_FOUND, "role_authority not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "role_authority found", "");
        roleAuthoritiesRepository.delete(optRoleAuthority.get());

        Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "role_authority deleted", "");
        response.setSuccessStatus(HttpStatus.OK);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping(path = {"/{roleId}/authorities"}, name = "roles-post-authorities-by-roleId")
    @PreAuthorize("hasAnyAuthority('roles-post-authorities-by-roleId', 'all')")
    public ResponseEntity<HttpReponse> postRoleAuthority(HttpServletRequest request,
            @RequestBody List<Authority> body,
            @PathVariable String roleId) throws Exception {
        String logprefix = request.getRequestURI() + " ";
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        HttpReponse response = new HttpReponse(request.getRequestURI());

        Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "", "");
        Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, body.toString(), "");

        Optional<Role> optRole = rolesRepository.findById(roleId);

        if (!optRole.isPresent()) {
            Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "role not found", "");
            response.setErrorStatus(HttpStatus.NOT_FOUND, "role not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "role found", "");

        RoleAuthority[] roleAuthorities = new RoleAuthority[body.size()];
        int i = 0;
        for (Authority authority : body) {

            Optional optAuthority = authoritiesRepository.findById(authority.getId());
            if (!optAuthority.isPresent()) {
                Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, "authority " + authority.getId() + " not found", "");
                response.setErrorStatus(HttpStatus.NOT_FOUND, "authority " + authority.getId() + " not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            RoleAuthority roleAuthority = new RoleAuthority();
            roleAuthority.setRoleId(roleId);
            roleAuthority.setAuthorityId(authority.getId());
            roleAuthoritiesRepository.save(roleAuthority);
            roleAuthorities[i] = roleAuthority;
            i = i++;
        }

        Logger.application.info(Logger.pattern, VersionHolder.VERSION, logprefix, i + "role_authorities created for roleId: " + roleId, "");
        response.setSuccessStatus(HttpStatus.CREATED);
        response.setData(roleAuthorities);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity handleExceptionBadRequestException(HttpServletRequest request, MethodArgumentNotValidException e) {
        String logprefix = request.getRequestURI() + " ";
        String location = Thread.currentThread().getStackTrace()[1].getMethodName();
        Logger.application.warn(Logger.pattern, VersionHolder.VERSION, logprefix, "validation failed", "");
        List<String> errors = e.getBindingResult().getFieldErrors().stream()
                .map(x -> x.getDefaultMessage())
                .collect(Collectors.toList());
        HttpReponse response = new HttpReponse(request.getRequestURI());
        response.setErrorStatus(HttpStatus.BAD_REQUEST);
        response.setData(errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
