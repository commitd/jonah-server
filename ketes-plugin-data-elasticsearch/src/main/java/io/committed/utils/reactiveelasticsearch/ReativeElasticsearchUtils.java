package io.committed.utils.reactiveelasticsearch;

import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.ListenableActionFuture;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
public class ReativeElasticsearchUtils {

  private ReativeElasticsearchUtils() {
    // Singleton
  }

  public static <T> Mono<T> toMono(final ListenableActionFuture<T> future) {
    return Mono.create(sink -> {
      future.addListener(new ActionListener<T>() {

        @Override
        public void onResponse(final T r) {
          sink.success(r);
        }

        @Override
        public void onFailure(final Exception e) {
          sink.error(e);

        }
      });

      sink.onCancel(() -> future.cancel(true));
    });
  }

  public static <T> Mono<T> convertSource(final ObjectMapper mapper, final byte[] source,
      final Class<T> clazz) {
    try {
      return Mono.just(mapper.readValue(source, clazz));
    } catch (final Exception e) {
      log.warn("Unable to deserialise source", e);
      return Mono.empty();
    }
  }
}
