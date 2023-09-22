package me.aias.service;

import io.milvus.client.MilvusClient;
import io.milvus.grpc.MutationResult;
import io.milvus.grpc.QueryResults;
import io.milvus.grpc.SearchResults;
import io.milvus.param.R;
import io.milvus.param.RpcStatus;
import me.aias.common.milvus.ConnectionPool;

import java.util.List;

public interface SearchService {
    // 重置向量引擎
    void clearSearchEngine();

    // 初始化向量引擎
    void initSearchEngine(Integer dimension);

    // 获取连接池
    ConnectionPool getConnectionPool(boolean refresh);

    // 获取Milvus Client
    MilvusClient getClient(ConnectionPool connPool);

    // 检查是否存在 collection
    void returnConnection(ConnectionPool connPool, MilvusClient client);

    // 检查是否存在 collection
    R<Boolean> hasCollection(MilvusClient milvusClient);
    R<Boolean> hasCollection();

    // 创建 collection
    R<RpcStatus> createCollection(MilvusClient milvusClient, Integer dimension, long timeoutMiliseconds) ;

    // 加载 collection
    R<RpcStatus> loadCollection(MilvusClient milvusClient);

    // 释放 collection
    R<RpcStatus> releaseCollection(MilvusClient milvusClient);

    // 删除 collection
    R<RpcStatus> dropCollection(MilvusClient milvusClient);

    // 创建 分区
    R<RpcStatus> createPartition(MilvusClient milvusClient, String partitionName);

    // 删除 分区
    R<RpcStatus> dropPartition(MilvusClient milvusClient, String partitionName);

    // 是否存在分区
    R<Boolean> hasPartition(MilvusClient milvusClient, String partitionName);

    // 创建 index
    R<RpcStatus> createIndex(MilvusClient client);

    // 删除 index
    R<RpcStatus> dropIndex(MilvusClient client);

    // 插入向量
    R<MutationResult> insert(List<Long> vectorIds, List<List<Float>> vectors);

    // 查询向量
    R<QueryResults> query(String expr);

    // 搜索向量
    R<SearchResults> search(Integer topK, List<List<Float>> vectorsToSearch);

    // 删除向量
    R<MutationResult> delete(String expr);
}
