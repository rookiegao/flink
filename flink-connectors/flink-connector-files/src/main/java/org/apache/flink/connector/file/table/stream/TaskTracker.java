/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.connector.file.table.stream;

import org.apache.flink.annotation.Internal;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

/**
 * Track the upstream tasks to determine whether all the upstream data of a checkpoint has been
 * received.
 */
@Internal
public class TaskTracker {

    private final int numberOfTasks;

    /** Checkpoint id to notified tasks. */
    private final TreeMap<Long, Set<Integer>> notifiedTasks = new TreeMap<>();

    public TaskTracker(int numberOfTasks) {
        this.numberOfTasks = numberOfTasks;
    }

    /**
     * @return true, if this checkpoint id need be committed.
     */
    public boolean add(long checkpointId, int task) {
        Set<Integer> tasks = notifiedTasks.computeIfAbsent(checkpointId, (k) -> new HashSet<>());
        tasks.add(task);
        if (tasks.size() == numberOfTasks) {
            notifiedTasks.headMap(checkpointId, true).clear();
            return true;
        }
        return false;
    }
}
