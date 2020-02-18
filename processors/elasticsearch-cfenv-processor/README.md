# Elasticsearch CFEnv Processor

Elasticsearch CFEnv Processor is a library that sets the properties in Spring Boot applications from the service entry in `VCAP_SERVICES` environment variable and autoconfigures elasticsearch clients such as `RestHighLevelClient` and `ReactiveElasticsearchClient`.

Autoconfiguration of the `RestHighLevelClient` and `ReactiveElasticsearchClient` beans can be managed by setting `IBM_CFENVPROCESSOR_ELASTICSEARCH_CLIENTS_ENABLE` environment variable. `IBM_CFENVPROCESSOR_ELASTICSEARCH_CLIENTS_ENABLE` environment variable accepts clients as comma separated values and by default autoconfigures `RestHighLevelClient` bean if the environment variable is not set.

## Examples

* If `IBM_CFENVPROCESSOR_ELASTICSEARCH_CLIENTS_ENABLE` is set as `rest,reactive` , the cfenv processor autoconfigures both `RestHighLevelClient` and `ReactiveElasticsearchClient`.
* If `IBM_CFENVPROCESSOR_ELASTICSEARCH_CLIENTS_ENABLE` is set as `rest` the cfenv processor autoconfigures `RestHighLevelClient`.
* If `IBM_CFENVPROCESSOR_ELASTICSEARCH_CLIENTS_ENABLE` is set as `reactive` the cfenv processor autoconfigures `ReactiveElasticsearchClient`.
