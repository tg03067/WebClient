package com.green.webclienttest.picsum;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.green.webclienttest.picsum.model.PicSumGetReq;
import com.green.webclienttest.picsum.model.PicSumGetRes;
import io.netty.channel.ChannelOption;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

@Service
@Slf4j
public class PicSumService {
    private final WebClient webClient;

    public PicSumService(){
        TcpClient tcpClient = TcpClient.create().
                option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
                // 불러오는 시간이 5초가 넘어가면 취소하겠다는 표시.

        ExchangeStrategies es = ExchangeStrategies.builder().
                codecs(config -> config.defaultCodecs().maxInMemorySize(-1)).
                //메모리 사용량 확인 / -1 : 사용제한 없음.
                build();
        this.webClient = WebClient.builder().
                exchangeStrategies(es).
                baseUrl("https://picsum.photos").
                clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient))).
                defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).
                build();
    }

    public List<PicSumGetRes> getPicSum(PicSumGetReq p) {
        String json = webClient.get().
                uri(uriBuilder -> uriBuilder.path("/v2/list").
                            queryParam("page", p.getPage()).
                            queryParam("limit", p.getLimit()).
                            build()
                    ).
                retrieve().
                bodyToMono(String.class).
                block();

        ObjectMapper om = new ObjectMapper().
                configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        List<PicSumGetRes> picSumList = null;

        try {
            JsonNode jsonNode = om.readTree(json);
            picSumList = om.convertValue(jsonNode.at(""),
                    new TypeReference<List<PicSumGetRes>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return picSumList;
    }
}

/*
    public PicSumService(){
        TcpClient tcpClient = TcpClient.create().
                option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);

        ExchangeStrategies es = ExchangeStrategies.builder().
                codecs(config -> config.defaultCodecs().maxInMemorySize(-1)).
//                codecs(new Consumer<ClientCodecConfigurer>() {
//                    @Override
//                    public void accept(ClientCodecConfigurer config) {
//                        config.defaultCodecs().maxInMemorySize(-1);
//                    }
//                }).
                build();
        this.webClient = WebClient.builder().
                exchangeStrategies(es).
                baseUrl("https://picsum.photos").
                clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient))).
                defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).
                build();
    }

 */