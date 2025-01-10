//package com.innovatrix.ahaar.service;
//
//import com.innovatrix.ahaar.model.ApplicationUser;
//import com.innovatrix.ahaar.model.ApplicationUserDTO;
//import com.innovatrix.ahaar.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class UserServiceTest {
//
//    @Mock
//    private UserRepository userRepository;
//
//    @InjectMocks
//    private UserService userService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void getUsers_ShouldReturnPagedUsers() {
//        // Arrange
//        ApplicationUser user = new ApplicationUser( "John", "john@example.com", "password123");
//        Page<ApplicationUser> page = new PageImpl<>(List.of(user));
//        when(userRepository.findAll(any(Pageable.class))).thenReturn(page);
//
//        // Act
//        Page<ApplicationUser> result = userService.getUsers(0, 10);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(1, result.getContent().size());
//        verify(userRepository, times(1)).findAll(PageRequest.of(0, 10));
//    }
//
//    @Test
//    void addUser_ShouldAddNewUser() {
//        // Arrange
//        ApplicationUserDTO userDTO = new ApplicationUserDTO("John", "john@example.com", "password123");
//        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.empty());
//
//        ApplicationUser user = new ApplicationUser(null, userDTO.getUserName(), userDTO.getEmail(), userDTO.getPassword());
//        when(userRepository.save(any(ApplicationUser.class))).thenReturn(user);
//        when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.of(user));
//
//        // Act
//        Optional<ApplicationUser> result = userService.addUser(userDTO);
//
//        // Assert
//        assertTrue(result.isPresent());
//        assertEquals(userDTO.getUserName(), result.get().getUserName());
//        verify(userRepository, times(1)).findByEmail(userDTO.getEmail());
//        verify(userRepository, times(1)).save(any(ApplicationUser.class));
//    }
//
//    @Test
//    void updateUser_ShouldUpdateExistingUser() {
//        // Arrange
//        Long userId = 1L;
//        ApplicationUserDTO userDTO = new ApplicationUserDTO("Jane", "jane@example.com", "newpassword123");
//        ApplicationUser user = new ApplicationUser( "John", "john@example.com", "password123");
//
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//        when(userRepository.save(any(ApplicationUser.class))).thenReturn(user);
//
//        // Act
//        ApplicationUser updatedUser = userService.updateUser(userId, userDTO);
//
//        // Assert
//        assertNotNull(updatedUser);
//        assertEquals(userDTO.getUserName(), updatedUser.getUserName());
//        assertEquals(userDTO.getEmail(), updatedUser.getEmail());
//        verify(userRepository, times(1)).findById(userId);
//        verify(userRepository, times(1)).save(user);
//    }
//
//    @Test
//    void deleteUser_ShouldRemoveUser() {
//        // Arrange
//        Long userId = 1L;
//        ApplicationUser user = new ApplicationUser( "John", "john@example.com", "password123");
//
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//
//        // Act
//        userService.deleteUser(userId);
//
//        // Assert
//        verify(userRepository, times(1)).findById(userId);
//        verify(userRepository, times(1)).deleteById(userId);
//    }
//
//    @Test
//    void getUserById_ShouldReturnUser() {
//        // Arrange
//        Long userId = 1L;
//        ApplicationUser user = new ApplicationUser( "John", "john@example.com", "password123");
//
//        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
//
//        // Act
//        Optional<ApplicationUser> result = userService.getUserById(userId);
//
//        // Assert
//        assertTrue(result.isPresent());
//        assertEquals(userId, result.get().getId());
//        verify(userRepository, times(1)).findById(userId);
//    }
//}
//
