package com.github.charlemaznable.bunny.rabbit.core.query;

import com.github.charlemaznable.bunny.client.domain.BunnyAddress;
import com.github.charlemaznable.bunny.client.domain.QueryRequest;
import com.github.charlemaznable.bunny.client.domain.QueryResponse;
import com.github.charlemaznable.bunny.plugin.BunnyHandler;
import com.github.charlemaznable.bunny.rabbit.dao.BunnyDao;
import com.github.charlemaznable.bunny.rabbit.mapper.ChargeCodeMapper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import lombok.val;

import javax.annotation.Nullable;

import static com.github.charlemaznable.bunny.plugin.elf.VertxElf.executeBlocking;
import static com.github.charlemaznable.bunny.rabbit.core.common.BunnyError.QUERY_FAILED;
import static com.github.charlemaznable.configservice.ConfigFactory.getConfig;
import static com.github.charlemaznable.core.lang.Condition.nullThen;
import static java.util.Objects.isNull;
import static org.n3r.eql.eqler.EqlerFactory.getEqler;

public final class QueryHandler
        implements BunnyHandler<QueryRequest, QueryResponse> {

    private final ChargeCodeMapper chargeCodeMapper;
    private final BunnyDao bunnyDao;

    public QueryHandler(@Nullable ChargeCodeMapper chargeCodeMapper,
                        @Nullable BunnyDao bunnyDao) {
        this.chargeCodeMapper = nullThen(chargeCodeMapper, () -> getConfig(ChargeCodeMapper.class));
        this.bunnyDao = nullThen(bunnyDao, () -> getEqler(BunnyDao.class));
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
        val serveName = request.getServeName();
        val chargeCode = chargeCodeMapper.chargeCode(serveName);

        executeBlocking(request.getContext(), block -> {
            val result = bunnyDao.queryChargingBalance(chargeCode);
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
