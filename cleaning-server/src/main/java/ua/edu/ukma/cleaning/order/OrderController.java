package ua.edu.ukma.cleaning.order;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ua.edu.ukma.cleaning.order.dto.*;
import ua.edu.ukma.cleaning.order.review.ReviewDto;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Order API", description = "Endpoint for operations order")
public class OrderController {
    private final OrderService orderService;

    @Operation(summary = "Get order for user by order id", description = "Get order for user by order id")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/user/{id}")
    public OrderForUserDto getOrderForUser(@PathVariable Long id) {
        return orderService.getOrderByIdForUser(id);
    }

    @Operation(summary = "Get order for admin by order id", description = "Get order for admin by order id")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/admin/{id}")
    public OrderForAdminDto getOrderForAdmin(@PathVariable Long id) {
        return orderService.getOrderByIdForAdmin(id);
    }

    @Operation(summary = "Get order for employee by order id", description = "Get order for employee by order id")
    @PreAuthorize("hasAuthority('ROLE_EMPLOYEE')")
    @GetMapping("/employee/{id}")
    public OrderForUserDto getOrderForEmployee(@PathVariable Long id) {
        return orderService.getOrderByIdForEmployee(id);
    }

    @Operation(summary = "Get all orders with status (pageable)", description = "Get all orders with status (pageable)")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/all/by-status")
    public OrderPageDto getAllOrdersByStatus(@RequestParam(defaultValue = "NOT_VERIFIED") Status status,
                                             @PageableDefault(sort = {"status","creationTime"}, direction = Sort.Direction.ASC) Pageable pageable) {
        return orderService.findOrdersByStatusAndPage(status, pageable);
    }
    @Operation(summary = "Get all orders (pageable)", description = "Get all orders (pageable)")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/all")
    public OrderPageDto getAllOrders(@PageableDefault(sort = {"status","creationTime"}, direction = Sort.Direction.ASC) Pageable pageable) {
        return orderService.findOrdersByPage(pageable);
    }

    @Operation(summary = "Get all orders by user id (pageable)", description = "Get all orders by user id (pageable)")
    @PreAuthorize("hasAnyAuthority('ROLE_USER','ROLE_ADMIN')")
    @GetMapping("/by-user/{email}")
    public OrderPageDto getOrdersOfUser(@PathVariable String email, @PageableDefault(sort = {"status","creationTime"}, direction = Sort.Direction.ASC) Pageable pageable) {
        return orderService.findOrdersByUserEmail(email, pageable);
    }

    @Operation(summary = "Get all orders of executor by id (pageable)", description = "Get all orders of executor by id (pageable)")
    @PreAuthorize("hasAnyAuthority('ROLE_EMPLOYEE','ROLE_ADMIN')")
    @GetMapping("/by-executor/{id}")
    public OrderPageDto getExecutorsOrders(@PathVariable Long id, @PageableDefault(sort = {"status","creationTime"}, direction = Sort.Direction.ASC) Pageable pageable) {
        return orderService.findOrdersByExecutorId(id, pageable);
    }

    @Operation(summary = "Update order for admin", description = "Update order for admin(executors, time)")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/admin")
    public OrderForAdminDto updateOrderByAdmin(@Valid @RequestBody OrderForAdminDto order) {
        return orderService.updateOrderForAdmin(order);
    }

    @Operation(summary = "Update order for user", description = "Update order for user(address, time)")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PutMapping("/user")
    public OrderForUserDto updateOrderByUser(@Valid @RequestBody OrderForUserDto order) {
        return orderService.updateOrderForUser(order);
    }


    @Operation(summary = "Create review for order", description = "Create review for order")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PutMapping("/update/review")
    public OrderForUserDto reviewOrder(@Valid @RequestBody ReviewDto review) {
        return orderService.updateReview(review);
    }

    @Operation(summary = "Create order", description = "Create order")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping
    public OrderForUserDto createOrder(@Valid @RequestBody OrderCreationDto order) {
        return orderService.createOrder(order);
    }

    @Operation(summary = "Delete order", description = "Delete order")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    @DeleteMapping("/{id}")
    public Boolean cancelOrder(@PathVariable Long id) {
        return orderService.cancelOrderById(id);
    }

    @Operation(summary = "Change order status", description = "Change order status")
    @PreAuthorize("hasAnyAuthority('ROLE_EMPLOYEE','ROLE_ADMIN')")
    @PutMapping("/change/status/{orderId}/{status}")
    public OrderListDto changeOrderStatus(@PathVariable Long orderId, @PathVariable Status status) {
        return orderService.changeOrderStatus(orderId, status);
    }
}
