package com.aliyun.openservices.spring.boot;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.Producer;
import com.aliyun.openservices.ons.api.SendCallback;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.order.OrderProducer;

class AliyunOnsMqApplicationTests {

    private final AliyunOnsMqTemplate template = new AliyunOnsMqTemplate(new AliyunOnsMqPoolProperties());

    @Test
    void sendsAsyncThroughProducer() {
        Producer producer = mock(Producer.class);
        Message message = new Message();

        assertTrue(template.sendAsyncMes(producer, message));
        verify(producer).sendAsync(same(message), any(SendCallback.class));
    }

    @Test
    void reportsAsyncSendFailure() {
        Producer producer = mock(Producer.class);
        Message message = new Message();
        doThrow(new IllegalStateException("send failed"))
                .when(producer).sendAsync(same(message), any(SendCallback.class));

        assertFalse(template.sendAsyncMes(producer, message));
    }

    @Test
    void sendsOrderedMessageWithShardingKey() {
        OrderProducer producer = mock(OrderProducer.class);
        SendResult result = mock(SendResult.class);
        Message message = new Message();
        message.setKey("message-key");
        when(producer.send(same(message), eq("shard-1"))).thenReturn(result);

        assertTrue(template.sendOrderMes(producer, message, "shard-1"));
        verify(producer).send(same(message), eq("shard-1"));
    }
}
