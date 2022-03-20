package io.goodforgod.simplelambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import io.goodforgod.simplelambda.http.EtherscanBlock;
import io.goodforgod.simplelambda.http.EtherscanHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 31.07.2021
 */
public class LambdaHandler implements RequestHandler<Request, Response> {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final EtherscanHttpClient etherscanHttpClient;

    public LambdaHandler(EtherscanHttpClient etherscanHttpClient) {
        this.etherscanHttpClient = etherscanHttpClient;
    }

    @Override
    public Response handleRequest(Request request, Context context) {
        logger.info("Processing Block with number: {}", request.blockNumber());
        final long started = System.currentTimeMillis();

        final EtherscanBlock block = etherscanHttpClient.getBlockByNumber(request.blockNumber());

        logger.info("Block retrieval took '{}' millis", System.currentTimeMillis() - started);
        return new Response(block.blockReward(), "Hello Miner - " + block.blockReward());
    }
}
