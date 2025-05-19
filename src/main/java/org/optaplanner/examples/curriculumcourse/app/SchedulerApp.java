package org.optaplanner.examples.curriculumcourse.app;

import org.optaplanner.core.api.solver.SolverJob;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.examples.curriculumcourse.domain.CourseSchedule;
import org.optaplanner.examples.curriculumcourse.persistence.CourseScheduleJsonImporter;
import org.optaplanner.examples.curriculumcourse.persistence.CourseScheduleJsonExporter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

public class SchedulerApp {

    private static final String OUTPUT_FILE = "solved_schedule.json";

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: java -jar scheduler.jar <schedule.json> <solverConfig.xml>");
            System.exit(1);
        }

        Path jsonPath = Paths.get(args[0]);
        Path solverConfigPath = Paths.get(args[1]);

        CourseSchedule unsolved = CourseScheduleJsonImporter.read(jsonPath);

        SolverConfig solverConfig = SolverConfig.createFromXmlFile(solverConfigPath.toFile());
        SolverManager<CourseSchedule, UUID> solverManager = SolverManager.create(solverConfig);

        UUID problemId = UUID.randomUUID();
        AtomicReference<CourseSchedule> bestSolutionRef = new AtomicReference<>(unsolved);

        SolverJob<CourseSchedule, UUID> job = solverManager.solveAndListen(
                problemId,
                id -> unsolved,
                bestSolution -> {
                    bestSolutionRef.set(bestSolution);
                    System.out.println("New best score: " + bestSolution.getScore());
                });

        Runtime.getRuntime().addShutdownHook(new Thread(() ->
                saveSolution(bestSolutionRef.get())));

        try {
            CourseSchedule finalBest = job.getFinalBestSolution();
            saveSolution(finalBest);
        } catch (ExecutionException | InterruptedException ex) {
            System.err.println("Solver interrupted: " + ex.getMessage());
            saveSolution(bestSolutionRef.get());
            Thread.currentThread().interrupt();
        } finally {
            solverManager.close();
        }
    }

    private static void saveSolution(CourseSchedule solution) {
        try {
            CourseScheduleJsonExporter.write(Paths.get(OUTPUT_FILE).toFile(), solution);
            System.out.println("\nSolution written to " + OUTPUT_FILE);
        } catch (Exception ex) {
            System.err.println("Failed to write solution: " + ex.getMessage());
        }
    }
}