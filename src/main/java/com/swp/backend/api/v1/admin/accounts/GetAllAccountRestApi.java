package com.swp.backend.api.v1.admin.accounts;

import com.google.gson.Gson;
import com.swp.backend.entity.RoleEntity;
import com.swp.backend.model.AccountModel;
import com.swp.backend.service.AccountService;
import com.swp.backend.service.RoleService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "api/v1/admin")
@AllArgsConstructor
public class GetAllAccountRestApi {
    private AccountService accountService;
    private Gson gson;
    private RoleService roleService;

    @GetMapping("all-account")
    public ResponseEntity<String> getAllUserHasRoleUserOrOwner() {
        try {
            Future<List<AccountModel>> accounts;
            Future<Integer> maxResult;
            ExecutorService executorService = Executors.newCachedThreadPool();
            accounts = executorService.submit(() -> accountService.getAllAccountHasRoleUser());
            maxResult = executorService.submit(() -> accountService.countAllAccountHasRoleUser());
            executorService.shutdown();
            boolean finished = executorService.awaitTermination(2, TimeUnit.MINUTES);
            if (finished) {
                GetAllAccountResponse response = GetAllAccountResponse.builder()
                        .accounts(accounts.get())
                        .maxResult(maxResult.get())
                        .build();
                return ResponseEntity.ok().body(gson.toJson(response));
            } else {
                return ResponseEntity.internalServerError().build();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("all-account")
    public ResponseEntity<String> getAllAccountHasRoleUserOrOwnerByFilter(@RequestBody(required = false) GetAllAccountRequest request) {
        if(request == null){
            List<?> resulSearch = accountService.searchAccount(null, null, null, null, null, null, null);
            return ResponseEntity.ok().body(gson.toJson(resulSearch));
        }
        List<?> resulSearch = accountService.searchAccount(request.getItemsPerPage(), request.getPage(), request.getRole(), request.getKeyword(), request.getStatus(), request.getSortBy(), request.getSort());
        return ResponseEntity.ok().body(gson.toJson(resulSearch));
    }

    @GetMapping("filter-account")
    public ResponseEntity<String> getAllFilterAccounts() {
        try {
            Future<FilterGroup<Boolean>> filterAccountState;
            Future<FilterGroup<Integer>> filterRole;
            ExecutorService executorService = Executors.newCachedThreadPool();
            filterAccountState = executorService.submit(() -> {
                Filter<Boolean> active = Filter.<Boolean>builder().textValue("Active").value(true).build();
                Filter<Boolean> unActive = Filter.<Boolean>builder().textValue("UnActive").value(false).build();
                return FilterGroup.<Boolean>builder().filterName("STATUS").filters(List.of(active, unActive)).build();
            });
            filterRole = executorService.submit(() -> {
                List<RoleEntity> roles = roleService.getRoleByListRoleName(List.of("user", "owner"));
                List<Filter<Integer>> roleFilters;
                roleFilters = roles.stream().map(role -> {
                    return Filter.<Integer>builder().textValue(role.getRoleName()).value(role.getId()).build();
                }).collect(Collectors.toList());
                return FilterGroup.<Integer>builder().filterName("ROLE").filters(roleFilters).build();
            });
            executorService.shutdown();
            if (executorService.awaitTermination(2, TimeUnit.MINUTES)) {
                List<FilterGroup<?>> filters = List.of(filterRole.get(), filterAccountState.get());
                return ResponseEntity.ok().body(gson.toJson(filters));
            } else {
                return ResponseEntity.internalServerError().build();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseEntity.ok().build();
        }
    }
}
