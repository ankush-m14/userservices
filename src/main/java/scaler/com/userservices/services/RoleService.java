package scaler.com.userservices.services;

import org.springframework.stereotype.Service;
import scaler.com.userservices.models.Role;
import scaler.com.userservices.repositeries.RoleRepository;

@Service
public class RoleService {
    private RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Role createRole(String name){
        Role role = new Role();
        role.setRole(name);
        return roleRepository.save(role);
    }
}
