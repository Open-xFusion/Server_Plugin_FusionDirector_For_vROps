/*
 * Copyright (c) xFusion Digital Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.xfusion.fd.config;

import org.springframework.core.log.LogFormatUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.UnknownHttpStatusCodeException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * RestTemplate错误处理
 *
 * @since 2023
 */
public class CustomRestTemplateResponseErrorHandler extends DefaultResponseErrorHandler {
    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        // 异常Code，业务代码自己处理
        return false;
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        HttpStatus statusCode = HttpStatus.resolve(response.getRawStatusCode());
        if (statusCode == null) {
            byte[] body = this.getResponseBody(response);
            String message = getErrorMessage(response.getRawStatusCode(), response.getStatusText(), body,
                    this.getCharset(response));
            throw new UnknownHttpStatusCodeException(message, response.getRawStatusCode(), response.getStatusText(),
                    response.getHeaders(), body, this.getCharset(response));
        } else {
            this.handleError(response, statusCode);
        }
    }

    private String getErrorMessage(int rawStatusCode, String statusText, @Nullable byte[] responseBody,
            @Nullable Charset charset) {
        String preface = rawStatusCode + " " + statusText + ": ";
        if (ObjectUtils.isEmpty(responseBody)) {
            return preface + "[no body]";
        } else {
            String bodyText = new String(responseBody, charset != null ? charset : StandardCharsets.UTF_8);
            bodyText = LogFormatUtils.formatValue(bodyText, -1, true);
            return preface + bodyText;
        }
    }
}
