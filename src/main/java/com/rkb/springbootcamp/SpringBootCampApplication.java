package com.rkb.springbootcamp;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

@SpringBootApplication
public class SpringBootCampApplication {
	public static void main(String[] args) {
		SpringApplication.run(SpringBootCampApplication.class, args);
	}

/*	@Component("uuid")
	public static class UuidService {
		public String buildUuid() {
			return UUID.randomUUID().toString();
		}
	}

	@Bean
	Bar bar(Foo foo, @Value("#{uuid.buildUuid()}") String uuid) {
		return new Bar(foo, uuid);
	}

	class Bar {
		private Foo foo;
		Bar(Foo foo, String uuid) {
			System.out.println(" uui=>"+uuid);
			this.foo = foo;
		}
	}*/


	@Component
	@Aspect
	class LoggingAspect {
		private final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);
		@Around("execution (* com.rkb..*.*() )")
		public Object log(ProceedingJoinPoint pjp) throws Throwable {
			logger.info("Before {}", pjp.toString());
			Object proceed = pjp.proceed();
			logger.info("After {}", pjp.toString());
			return proceed;
		}
	}

	@Component("uuid")
	class UuidService {
		public String buildUuid() {
			return UUID.randomUUID().toString();
		}
	}
	@Component
	class Bar {
		private Foo foo;
		private final Logger logger = LoggerFactory.getLogger(Bar.class);
		Bar(Foo foo,
			@Value("#{uuid.buildUuid()}") String uuid,
			@Value("#{2>1}") boolean proceed) {
			logger.info("Uui=>"+uuid);
			logger.info("Proceed=>"+proceed);
			this.foo = foo;
		}
	}
	@Component
	class Foo {

	}

	@Bean
	RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@RestController
	class IsbnController {
		private RestTemplate restTemplate;
		IsbnController(RestTemplate restTemplate) {
			this.restTemplate = restTemplate;
		}
		//http://localhost:8081/books/978-3-16-148410-0
		@GetMapping("/books/{isbn}")
		public String lookByIsbn(@PathVariable("isbn") String isbn) {
			ResponseEntity<String> exchange = restTemplate.exchange("https://www.googleapis.com/books/v1/volumes?q="+isbn, HttpMethod.GET, null, String.class);
			String body = exchange.getBody();
			return body;
		}
	}

	@Component
	class LoggingFilter implements Filter {
		private final Logger logger = LoggerFactory.getLogger(Bar.class);
		@Override
		public void init(FilterConfig filterConfig) throws ServletException {

		}

		@Override
		public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
			Assert.isTrue(servletRequest instanceof HttpServletRequest, "this assume you have http request");
			HttpServletRequest httpServletRequest = HttpServletRequest.class.cast(servletRequest);
			String requestURI = httpServletRequest.getRequestURI();
			this.logger.info("new request is {}", requestURI);
			long time = System.currentTimeMillis();
			filterChain.doFilter(servletRequest, servletResponse);
			long delta = System.currentTimeMillis() - time;
			this.logger.info("request for uri {} took {} ms.", requestURI, delta);

		}

		@Override
		public void destroy() {

		}
	}

}
