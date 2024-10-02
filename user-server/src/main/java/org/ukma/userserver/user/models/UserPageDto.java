package org.ukma.userserver.user.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data

public class UserPageDto {
    private long currPage;
    private long numOfPages;
    private List<UserListDto> orderList;
}
