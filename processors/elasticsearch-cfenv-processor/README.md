# Elasticsearch CFEnv Processor

Elasticsearch CFEnv Processor is a library that sets the properties in Spring Boot applications from the service entry in `VCAP_SERVICES` environment variable and autoconfigures elasticsearch clients such as `RestHighLevelClient` and `ReactiveElasticsearchClient`.

Autoconfiguration of the `RestHighLevelClient` and `ReactiveElasticsearchClient` beans can be managed by setting `IBM_CFENVPROCESSOR_ELASTICSEARCH_CLIENTS_ENABLE` environment variable. `IBM_CFENVPROCESSOR_ELASTICSEARCH_CLIENTS_ENABLE` environment variable accepts clients as comma separated values and by default autoconfigures `RestHighLevelClient` bean if the environment variable is not set.

## Elasticsearch client configuration

* If `IBM_CFENVPROCESSOR_ELASTICSEARCH_CLIENTS_ENABLE` is set to `rest,reactive` , the cfenv processor autoconfigures both `RestHighLevelClient` and `ReactiveElasticsearchClient`.
* If `IBM_CFENVPROCESSOR_ELASTICSEARCH_CLIENTS_ENABLE` is set to `rest` the cfenv processor autoconfigures `RestHighLevelClient`.
* If `IBM_CFENVPROCESSOR_ELASTICSEARCH_CLIENTS_ENABLE` is set to `reactive` the cfenv processor autoconfigures `ReactiveElasticsearchClient`.

### RestHighLevelClient
To create an instance of RestHighLevelClient

```
@Autowired
RestHighLevelClient restHighLevelClient;
```

To get the low-level rest client that the current high-level client instance is using to perform requests

```
RestClient lowLevelClient = restHirestHighLevelClient.getLowLevelClient();
```


### ReactiveElasticsearchClient
To create an instance of ReactiveElasticsearchClient

```
@Autowired
ReactiveElasticsearchClient reactiveClient;
```
