package ua.edu.ukma.cleaning.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderPageDto {
    private long currPage;
    private long numOfPages;
    private List<OrderListDto> orderList;
}
