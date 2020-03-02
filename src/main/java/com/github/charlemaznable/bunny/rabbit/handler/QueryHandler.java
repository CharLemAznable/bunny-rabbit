package com.github.charlemaznable.bunny.rabbit.handler;

import com.github.charlemaznable.bunny.client.domain.BunnyAddress;
import com.github.charlemaznable.bunny.client.domain.QueryRequest;
import com.github.charlemaznable.bunny.client.domain.QueryResponse;
import com.github.charlemaznable.bunny.rabbit.handler.dao.BunnyDao;
import com.github.charlemaznable.bunny.rabbit.vertx.common.BunnyHandler;
import com.google.inject.Inject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;

import static com.github.charlemaznable.bunny.client.domain.BunnyBaseResponse.RESP_CODE_OK;
import static com.github.charlemaznable.bunny.client.domain.BunnyBaseResponse.RESP_DESC_SUCCESS;
import static com.github.charlemaznable.bunny.rabbit.handler.common.BunnyBizError.QUERY_FAILED;
import static com.github.charlemaznable.core.lang.Condition.nullThen;
import static java.util.Objects.nonNull;
import static org.n3r.eql.eqler.EqlerFactory.getEqler;

@Slf4j
@Component
public final class QueryHandler
        implements BunnyHandler<QueryRequest, QueryResponse> {

    private final BunnyDao bunnyDao;

    public QueryHandler() {
        this(null);
    }

    @Inject
    @Autowired
    public QueryHandler(@Nullable BunnyDao bunnyDao) {
        this.bunnyDao = nullThen(bunnyDao, () -> getEqler(BunnyDao.class));
    }

    @Override
    public String address() {
        return BunnyAddress.QUERY;
    }

    @Override
    public Class<QueryRequest> getRequestClass() {
        return QueryRequest.class;
    }

    @Override
    public void execute(QueryRequest request,
                        Handler<AsyncResult<QueryResponse>> handler) {
        executeBlocking(future -> {
            val chargingType = request.getChargingType();

            try {
                val response = new QueryResponse();
                response.setChargingType(chargingType);
                val result = bunnyDao.queryChargingBalance(chargingType);
                if (nonNull(result)) {
                    response.setRespCode(RESP_CODE_OK);
                    response.setRespDesc(RESP_DESC_SUCCESS);
                    response.setBalance(result.getBalance());
                    response.setUnit(result.getUnit());
                } else {
                    response.setRespCode(QUERY_FAILED.respCode());
                    response.setRespDesc(QUERY_FAILED.respDesc());
                }
                future.complete(response);

            } catch (Exception e) {
                log.warn("Query balance of chargingType:{} failed:\n", chargingType, e);
                future.fail(e);
            }
        }, handler);
    }
}
