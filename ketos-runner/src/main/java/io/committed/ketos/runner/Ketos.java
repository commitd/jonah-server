package io.committed.ketos.runner;

import org.springframework.boot.SpringApplication;

import io.committed.invest.server.core.annotations.InvestApplication;

/** Main class for Ketos. */
@InvestApplication
public class Ketos {

  public static void main(final String[] args) {
    SpringApplication.run(Ketos.class, args);
  }
}
