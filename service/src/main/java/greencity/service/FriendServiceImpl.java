package greencity.service;

import greencity.constant.ErrorMessage;
import greencity.dto.PageableDto;
import greencity.dto.friends.UserFriendDto;
import greencity.dto.user.UserManagementDto;
import greencity.entity.User;
import greencity.exception.exceptions.NotFoundException;
import greencity.mapping.UserFriendDtoMapper;
import greencity.repository.UserRepo;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FriendServiceImpl implements FriendService{
    private final UserRepo userRepo;
    private final ModelMapper modelMapper;

    @Override
    public PageableDto<UserFriendDto> getAllFriendsOfUser(long userId, Pageable pageable) {
        validateUserExistence(userId);

        Page<User> friends =userRepo.getAllFriendsOfUserIdPage(userId, pageable);

        List<UserFriendDto> friendList =
                friends.stream().map(friend -> modelMapper.map(friend, UserFriendDto.class))
                        .toList();

        return new PageableDto<>(
                friendList,
                friends.getTotalElements(),
                friends.getPageable().getPageNumber(),
                friends.getTotalPages());
    }


    private void validateUserExistence(long userId) {
        if (!userRepo.existsById(userId)) {
            throw new NotFoundException(ErrorMessage.USER_NOT_FOUND_BY_ID + userId);
        }
    }
}
