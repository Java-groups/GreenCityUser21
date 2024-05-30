package greencity.service;

import greencity.dto.PageableDto;
import greencity.dto.friends.UserFriendDto;
import greencity.dto.user.UserManagementDto;

import java.awt.print.Pageable;

public interface FriendService {
    PageableDto<UserFriendDto> findAllFriendsOfUser(long userId, String name);

    PageableDto<UserManagementDto> findUserFriendsByUserId(Pageable page, long userId);
}
