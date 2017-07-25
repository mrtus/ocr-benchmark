package be.mrtus.ocrbenchmark.domain;

import be.mrtus.ocrbenchmark.application.config.properties.BenchmarkConfig;
import be.mrtus.ocrbenchmark.domain.entities.BenchmarkResult;
import be.mrtus.ocrbenchmark.domain.entities.LoadedFile;
import be.mrtus.ocrbenchmark.domain.entities.ProcessResult;
import be.mrtus.ocrbenchmark.domain.libraries.OCRLibrary;
import be.mrtus.ocrbenchmark.persistence.ProcessResultRepository;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

public class Processor extends Thread {

	private final BenchmarkConfig config;
	private final FileLoader fileLoader;
	private final int id;
	private final OCRLibrary library;
	private final Logger logger = Logger.getLogger(Processor.class.getName());
	private final BenchmarkResult result;
	private final ProcessResultRepository resultRepository;

	public Processor(
			int id,
			BenchmarkConfig config,
			FileLoader fileLoader,
			ProcessResultRepository resultRepository,
			BenchmarkResult result,
			OCRLibrary library
	) {
		this.id = id;
		this.config = config;
		this.fileLoader = fileLoader;
		this.resultRepository = resultRepository;
		this.result = result;
		this.library = library;

		this.setDaemon(true);
		this.setName("Benchmark Processor " + this.id);
	}

	@Override
	public void run() {
		ArrayBlockingQueue<LoadedFile> queue = this.fileLoader.getQueue();

		while(true) {
			while(queue.peek() == null) {
				if(!this.fileLoader.isLoading()) {
					break;
				}
			}

			LoadedFile lf = queue.poll();

			if(lf == null) {
				break;
			}

			this.library.doOCR(lf);
		}
	}

	private void processFile(LoadedFile lf) {
		this.logger.info("Processing file " + lf.getPath().toString());

		try {
			HttpClientBuilder httpBuilder = HttpClientBuilder.create();
			RequestBuilder requestBuilder = RequestBuilder.post();

			URL url = new URL(this.config.getProcessorUrl());

			requestBuilder.setUri(url.toURI());

			HttpEntity requestEntity = new StringEntity(lf.getFileContents());
			requestBuilder.setEntity(requestEntity);

			HttpClient httpClient = httpBuilder.build();
			HttpUriRequest httpRequest = requestBuilder.build();

			long start = System.currentTimeMillis();

			HttpResponse response = httpClient.execute(httpRequest);

			long end = System.currentTimeMillis();

			String responseResult = this.processResponse(response);

			ProcessResult processResult = new ProcessResult();

			processResult.setBenchmarkResult(this.result);
			processResult.setPath(lf.getPath());
			processResult.setResult(responseResult);
			processResult.setDuration(end - start);

			this.resultRepository.save(processResult);
		} catch(MalformedURLException | URISyntaxException ex) {
			this.logger.log(Level.SEVERE, null, ex);
		} catch(IOException ex) {
			this.logger.log(Level.SEVERE, null, ex);
		}
	}

	private String processResponse(HttpResponse response) {
		InputStream inputStream = null;
		try {
			HttpEntity body = response.getEntity();

			inputStream = body.getContent();

			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

			String responseResult = "";

			String line = "";

			while((line = br.readLine()) != null) {
				responseResult += line;
			}

			return responseResult;
		} catch(MalformedURLException ex) {
			this.logger.log(Level.SEVERE, null, ex);
		} catch(IOException ex) {
			this.logger.log(Level.SEVERE, null, ex);
		} finally {
			try {
				inputStream.close();
			} catch(IOException ex) {
				this.logger.log(Level.SEVERE, null, ex);
			}
		}
		return null;
	}
}
