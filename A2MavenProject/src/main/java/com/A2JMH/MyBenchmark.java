/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.A2JMH;

// you'll need a lot of imports to do what you want in JMH...
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Level;

import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

// Annotations to specify the mode and the time units desired
	@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
	public class MyBenchmark {

// ------------ State classes ---------------------------------------------------------------------------------------------------------------------------------------------------
		
		// Library State with LOW readers (18)
		@State(Scope.Benchmark)
			public static class LibraryStateLo {

				// wariables
				int numWatchers;
				ExecutorService eS;
				Phaser ph;

				// A standard Reentrant Lock to handle this program's concurrency
				ReentrantLock r;

				// setup method called at each invocation of the corresponding benchmark method
				@Setup(Level.Invocation)
					public void setup() {
						numWatchers = 18;
						eS = Executors.newFixedThreadPool(2 + numWatchers);
						ph = new Phaser(1);
						r = new ReentrantLock();
					}

				// teardown method called when each invocation of the corresponding benchmark method finishes
				@TearDown(Level.Invocation)
					public void teardown(){
						ph.arriveAndDeregister();
						eS.shutdown();
						try {
							// wait 8 seconds before termination
							if (!eS.awaitTermination(8000, TimeUnit.MILLISECONDS)) {
								eS.shutdownNow();
							}
						} catch (InterruptedException e) {
							eS.shutdownNow();
						}
					}
			}


		// Library State with HIGH readers (198)
		@State(Scope.Benchmark)
			public static class LibraryStateHi {

				// wariables
				int numWatchers;
				ExecutorService eS;
				Phaser ph;
				ReentrantLock r;

				@Setup(Level.Invocation)
					public void setup() {
						numWatchers = 198;
						eS = Executors.newFixedThreadPool(2 + numWatchers);
						ph = new Phaser(1);
						r = new ReentrantLock();
					}

				@TearDown(Level.Invocation)
					public void teardown(){
						ph.arriveAndDeregister();
						eS.shutdown();
						try {
							if (!eS.awaitTermination(8000, TimeUnit.MILLISECONDS)) {
								eS.shutdownNow();
							}
						} catch (InterruptedException e) {
							eS.shutdownNow();
						}
					}
			}


		// My State with LOW readers (18)
		@State(Scope.Benchmark)
			public static class MyStateLo {

				// wariables
				int numWatchers;
				ExecutorService eS;
				Phaser ph;

				// A WatchPlay Lock to implement concurrency in my own test case
				WatchPlayLock wPL;

				@Setup(Level.Invocation)
					public void setup() {
						numWatchers = 18;
						eS = Executors.newFixedThreadPool(2 + numWatchers);
						ph = new Phaser(1);
						wPL = new WatchPlayLock();	
					}

				@TearDown(Level.Invocation)
					public void teardown(){
						ph.arriveAndDeregister();
						eS.shutdown();
						try {
							if (!eS.awaitTermination(8000, TimeUnit.MILLISECONDS)) {
								eS.shutdownNow();
							}
						} catch (InterruptedException e) {
							eS.shutdownNow();
						}
					}
			}


		// My State with HIGH readers (198)
		@State(Scope.Benchmark)
			public static class MyStateHi {

				// wariables
				int numWatchers;
				ExecutorService eS;
				Phaser ph;
				WatchPlayLock wPL;

				@Setup(Level.Invocation)
					public void setup() {
						numWatchers = 198;
						eS = Executors.newFixedThreadPool(2 + numWatchers);
						ph = new Phaser(1);
						wPL = new WatchPlayLock();	
					}

				@TearDown(Level.Invocation)
					public void teardown(){
						ph.arriveAndDeregister();
						eS.shutdown();
						try {
							if (!eS.awaitTermination(8000, TimeUnit.MILLISECONDS)) {
								eS.shutdownNow();
							}
						} catch (InterruptedException e) {
							eS.shutdownNow();
						}
					}
			}

// ------------ Benchmark methods -----------------------------------------------------------------------------------------------------------------------------------------------

		// Reentrant Lock, Low readers
		@Benchmark
			@Fork(value = 1)
			@Measurement(iterations = 20, time = 1)
			@Warmup(iterations = 5, time = 1)
			public void libraryLo(LibraryStateLo msl) {

				// execute the Watchers after both players make their move
				for (int j = 0; j < msl.numWatchers; j++) {
					msl.eS.execute(new WatcherTaskA("Watcher #" + j, msl.r));
				}

				// execute your players on the board.
				msl.eS.execute(new WhitePlayerTaskA("White Player", msl.r, msl.ph));
				msl.eS.execute(new BlackPlayerTaskA("Black Player", msl.r, msl.ph));
				msl.ph.arriveAndAwaitAdvance();
			}

		// Reentrant Lock, High readers
		@Benchmark
			@Fork(value = 1)
			@Measurement(iterations = 20, time = 1)
			@Warmup(iterations = 5, time = 1)
			public void libraryHi(LibraryStateHi msl) {

				// execute the Watchers after both players make their move
				for (int j = 0; j < msl.numWatchers; j++) {
					msl.eS.execute(new WatcherTaskA("Watcher #" + j, msl.r));
				}

				// execute your players on the board.
				msl.eS.execute(new WhitePlayerTaskA("White Player", msl.r, msl.ph));
				msl.eS.execute(new BlackPlayerTaskA("Black Player", msl.r, msl.ph));
				msl.ph.arriveAndAwaitAdvance();
			}

		// WatchPlay Lock, Low readers
		@Benchmark
			@Fork(value = 1)
			@Measurement(iterations = 20, time = 1)
			@Warmup(iterations = 5, time = 1)
			public void MyLo(MyStateLo msl) {

				// execute the Watchers after both players make their move
				for (int j = 0; j < msl.numWatchers; j++) {
					msl.eS.execute(new WatcherTaskB("Watcher #" + j, msl.wPL));
				}

				// execute your players on the board.
				msl.eS.execute(new WhitePlayerTaskB("White Player", msl.wPL, msl.ph));
				msl.eS.execute(new BlackPlayerTaskB("Black Player", msl.wPL, msl.ph));
				msl.ph.arriveAndAwaitAdvance();
			}

		// WatchPlay Lock, High readers	
		@Benchmark
			@Fork(value = 1)
			@Measurement(iterations = 20, time = 1)
			@Warmup(iterations = 5, time = 1)
			public void MyHi(MyStateHi msl) {

				// execute the Watchers after both players make their move
				for (int j = 0; j < msl.numWatchers; j++) {
					msl.eS.execute(new WatcherTaskB("Watcher #" + j, msl.wPL));
				}

				// execute your players on the board.
				msl.eS.execute(new WhitePlayerTaskB("White Player", msl.wPL, msl.ph));
				msl.eS.execute(new BlackPlayerTaskB("Black Player", msl.wPL, msl.ph));
				msl.ph.arriveAndAwaitAdvance();
			}
	}

