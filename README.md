# High-Performance Asynchronous Batch Processing in Java

##  Overview

This project is a comprehensive case study demonstrating how to evolve a simple, slow, synchronous file processing service into a high-performance, resilient, and observable asynchronous system in Java using Spring Boot.

The primary goal is to process a large CSV file (containing 1 million user records) efficiently without blocking the client's HTTP request. We start with a naive synchronous implementation and refactor it step-by-step into a concurrent, batch-processing model powered by a fine-tuned `ThreadPoolExecutor`.

**Performance Highlights:**
* **API Responsiveness:** Reduced from a **~12-second** blocking wait to an **immediate (<100ms)** response.
* **Background Throughput:** Total processing time for 1 million records was cut by more than half, from **~12 seconds to ~5.4 seconds**.
* **System Stability:** The final architecture can handle high load gracefully using a back-pressure mechanism, preventing crashes from resource exhaustion.

---

## 💡 Key Concepts Demonstrated

This repository is a practical guide to the following core Java and Spring concepts:

* **`ThreadPoolExecutor` Deep Dive:**
    * Detailed configuration of all 7 core parameters.
    * Understanding the internal `execute()` workflow.
    * Choosing parameters based on task characteristics (CPU-bound vs. I/O-bound).

* **`BlockingQueue` Strategy:**
    * The critical choice of a bounded `ArrayBlockingQueue` to prevent `OutOfMemoryError`.
    * Analysis of other queue types (`LinkedBlockingQueue`, `SynchronousQueue`) and their impact.

* **`RejectedExecutionHandler` in Action:**
    * Practical implementation and observation of the `CallerRunsPolicy` as a back-pressure mechanism.

* **Spring Boot Asynchronous Programming:**
    * Effective use of `@EnableAsync` and `@Async`.
    * Troubleshooting and solving the `@Async` self-invocation failure via `@Lazy` to break circular dependencies.

* **Concurrency & Batching Synergy:**
    * Combining the power of multi-threading with the efficiency of database batch operations (`saveAll`).
    * Finding the optimal task granularity (processing batches of records vs. single records).

* **Full Observability Stack:**
    * Setting up a complete monitoring pipeline with **Actuator, Micrometer, Prometheus, and Grafana**.
    * Creating custom dashboards to monitor thread pool metrics in real-time.

* **Performance Engineering Lifecycle:**
    * Establishing a baseline, load testing with **JMeter**, analyzing metrics, and iterative tuning.
    * Diagnosing and solving real-world production issues like connection pool exhaustion and long-tail latencies (GC pauses).

---

## 🚀 How to Run

1.  **Prerequisites:**
    * Java 17+
    * Apache Maven 3.8+
    * Docker & Docker Compose (for the monitoring stack)

2.  **Build the Application:**
    ```bash
    mvn clean install
    ```

3.  **Run the Spring Boot Application:**
    ```bash
    java -jar target/thread-pool-demo-0.0.1-SNAPSHOT.jar
    ```
    The application will start on `http://localhost:8080`.

---

## 🔬 How to Test & Observe

To replicate the performance tests and observe the results:

1.  **Generate Test Data:**
    A Python script `generate_data.py` blueprint is available in the course history. You'll need to create it and run it to generate the `users_1M.csv` file.
    ```bash
    # Install dependencies
    pip install Faker tqdm
    # Run script
    python generate_data.py
    ```

2.  **Start the Monitoring Stack:**
    Navigate to the `monitoring/` directory and run:
    ```bash
    docker-compose up -d
    ```
    * Prometheus will be available at `http://localhost:9090`.
    * Grafana will be available at `http://localhost:3000` (login: `admin`/`admin`).

3.  **Configure Grafana:**
    * Add Prometheus as a data source (`http://prometheus:9090`).
    * Import a JVM dashboard (e.g., ID `4701`) and add a new panel to monitor the `executor_queued_tasks` and `executor_active_threads` for the `name="taskExecutor"`.

4.  **Run the Load Test:**
    * Use Apache JMeter to create a test plan that sends a `POST` request with the `users_1M.csv` file to the `http://localhost:8080/upload-async` endpoint.
    * Start with a low number of concurrent users (e.g., 5) and increase the load while observing the Grafana dashboard.