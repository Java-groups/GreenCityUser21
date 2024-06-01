package greencity.service;

import greencity.ModelUtils;
import greencity.TestConst;
import greencity.dto.PageableDto;
import greencity.dto.friends.UserFriendDto;
import greencity.dto.user.UserVO;
import greencity.entity.User;
import greencity.enums.Role;
import greencity.repository.UserRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class FriendServiceImplTest {
    @Mock
    UserRepo userRepo;

    @Mock
    ModelMapper modelMapper;

    @InjectMocks
    FriendServiceImpl friendService;

    @Test
    void getAllFriendsOfUser() {

        long userId = 2L;
        int page = 0;
        int size = 1;
        long totalElements = 50;
        Pageable pageable = PageRequest.of(page, size);
        UserFriendDto expectedResult = ModelUtils.getUserFrindDto();

        User user = User.builder()
                .id(1L)
                .name(TestConst.NAME)
                .email(TestConst.EMAIL)
                .build();

        Page<User> UserFriendsPage = new PageImpl<>(List.of(user), pageable, totalElements);


        when(userRepo.existsById(userId)).thenReturn(true);
        when(userRepo.getAllFriendsOfUserIdPage(userId, pageable)).thenReturn(UserFriendsPage);
        when(modelMapper.map(user, UserFriendDto.class)).thenReturn(expectedResult);


        PageableDto<UserFriendDto> pageableDto =
                friendService.getAllFriendsOfUser(userId, pageable);

        assertNotNull(pageableDto);
        assertNotNull(pageableDto.getPage());
        assertEquals(1, pageableDto.getPage().size());
        assertEquals(totalElements, pageableDto.getTotalElements());
        assertEquals(totalElements, pageableDto.getTotalPages());
        assertEquals(page, pageableDto.getCurrentPage());
        assertEquals(expectedResult, pageableDto.getPage().get(0));


        verify(userRepo).existsById(userId);
        verify(userRepo).getAllFriendsOfUserIdPage(userId, pageable);

    }
}