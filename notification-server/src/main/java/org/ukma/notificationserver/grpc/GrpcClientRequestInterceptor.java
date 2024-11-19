package org.ukma.notificationserver.grpc;

import io.grpc.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.ukma.notificationserver.security.JwtService;
import org.ukma.notificationserver.user.UserServerClient;

@RequiredArgsConstructor
public class GrpcClientRequestInterceptor implements ClientInterceptor  {

    private final UserServerClient userServerClient;
    private final JwtService jwtService;

    public <T, V> ClientCall<T, V> interceptCall(
            final MethodDescriptor<T, V> methodDescriptor,
            final CallOptions callOptions,
            final Channel channel) {

        return new ForwardingClientCall.SimpleForwardingClientCall<T, V>(
                channel.newCall(methodDescriptor, callOptions)) {

            private String token;

            @Override
            public void start(ClientCall.Listener<V> responseListener, Metadata headers) {
                if(token == null || jwtService.isTokenExpired(token)) {
                    token = userServerClient.getJWTToken();
                }
                headers.put(Metadata.Key.of(HttpHeaders.AUTHORIZATION, Metadata.ASCII_STRING_MARSHALLER), token);
                super.start(responseListener, headers);
            }
        };
    }
}
