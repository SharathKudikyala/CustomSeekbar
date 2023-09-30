package com.app.customseekbar.custom;


    import java.util.Arrays;

    class TaskScheduler {
        public int minMachinesRequired(int[] start, int[] end) {
            if (start == null || end == null || start.length != end.length) {
                return 0;
            }

            int n = start.length;
            int[] sortedEnd = Arrays.copyOf(end, n);
            Arrays.sort(start);
            Arrays.sort(sortedEnd);

            int machines = 0;
            int endIndex = 0;

            for (int i = 0; i < n; i++) {
                if (start[i] <= sortedEnd[endIndex]) {
                    machines++;
                } else {
                    endIndex++;
                }
            }

            return machines;
        }

        public static void main(String[] args) {
            int n = 5;
            int[] start = {1, 8, 3, 9, 6};
            int[] end = {17, 9, 6, 25, 14, 7};

            TaskScheduler scheduler = new TaskScheduler();
            int result = scheduler.minMachinesRequired(start, end);
            System.out.println("Number of machines required: " + result);
        }
    }


