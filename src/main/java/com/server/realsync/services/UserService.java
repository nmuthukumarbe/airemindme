package com.server.realsync.services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.server.realsync.entity.CustomUserDetails;
import com.server.realsync.entity.Role;
import com.server.realsync.entity.User;
import com.server.realsync.repo.UserRepository;

@Service
public class UserService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		Collection<Role> roles_in = new ArrayList<>();
		roles_in.add(user.getRole());
		// return org.springframework.security.core.userdetails.User
		// .withUsername(user.getUsername())
		// .password(user.getPassword())
		// .authorities(mapRolesToAuthorities(roles_in))
		// .build();
		return new CustomUserDetails(user);
	}

	public User findByUsername(String username) {
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));
	}
	
	public User findByUserId(long userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));
	}
	
	public Optional<User> findUserById(long userId) {
		return userRepository.findById(userId);
	}

	public User saveUser(User user) {
		return userRepository.save(user);
	}

	public List<User> getUsersByAccountId(Integer accountId) {
        return userRepository.findByAccountId(accountId);
    }
	
	public List<User> findByAccountIdAndRoleId(Integer accountId, Long roleId) {
        return userRepository.findByAccountIdAndRoleId(accountId, roleId);
    }
	
	
	public long countByRoleId(Long roleId) {
        return userRepository.countByRoleId(roleId);
    }
	
	public long countByAccountId(Integer accountId) {
        return userRepository.countByAccountId(accountId);
    }
	
}
