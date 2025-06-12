package com.keepsafe.notes.security.services;

import com.keepsafe.notes.models.User;
import com.keepsafe.notes.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    //class to determine how user data is loaded by making use of UserDetailsService that allows us to load user specific data from our custom model during authentication process.

    //if both UserDetailsImpl and UserDetailsServiceImpl are not defined then spring security will not be able to understand custom attributes provided to the users. It relies on UserDetails object because it wants to retrieve the user info and without these impl, it will not know on how to access the custom attributes as it doesn't exist in inbuilt implementation. It will also not know how to load these custom user data. These two classes also allows us to customize our authentication logic.
    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        return UserDetailsImpl.build(user); //building the user once found, then return the object of userDetails of them.
    }
}
