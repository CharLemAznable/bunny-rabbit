package com.github.charlemaznable.bunny.rabbit.core.query;

import com.github.charlemaznable.bunny.client.domain.BunnyAddress;
import com.github.charlemaznable.bunny.client.domain.QueryRequest;
import com.github.charlemaznable.bunny.client.domain.QueryResponse;
import com.github.charlemaznable.bunny.rabbit.core.common.BunnyHandler;
import com.github.charlemaznable.bunny.rabbit.dao.BunnyDao;
import com.google.inject.Inject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;

import static com.github.charlemaznable.bunny.rabbit.core.common.BunnyError.QUERY_FAILED;
import static com.github.charlemaznable.bunny.rabbit.core.common.BunnyHandlerElf.executeBlocking;
import static com.github.charlemaznable.core.lang.Condition.nullThen;
import static java.util.Objects.isNull;
import static org.n3r.eql.eqler.EqlerFactory.getEqler;

@Component
public final class QueryHandler
        implements BunnyHandler<QueryRequest, QueryResponse> {

    private final BunnyDao bunnyDao;

    @Inject
    @Autowired
    public QueryHandler(@Nullable BunnyDao bunnyDao) {
        this.bunnyDao = nullThen(bunnyDao,
                () -> getEqler(BunnyDao.class));
    }

    @Override
    public String address() {
        return BunnyAddress.QUERY;
    }

    @Override
    public Class<? extends QueryRequest> getRequestClass() {
        return QueryRequest.class;
    }

    @Override
    public void execute(QueryRequest request,
                        Handler<AsyncResult<QueryResponse>> handler) {
        val response = request.createResponse();
        val chargingType = request.getChargingType();

        executeBlocking(block -> {
            val result = bunnyDao.queryChargingBalance(chargingType);
            if (isNull(result)) {
                block.fail(QUERY_FAILED.exception());
                return;
            }
            response.succeed();
            response.setBalance(result.getBalance());
            response.setUnit(result.getUnit());
            block.complete(response);
        }, handler);
    }
}