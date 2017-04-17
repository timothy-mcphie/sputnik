package pl.touk.sputnik.processor.pylint;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import pl.touk.sputnik.exec.ExternalProcess;

import javax.annotation.Nullable;
import java.util.List;

@Slf4j
class PylintExecutor {
    private static final String PIT_EXECUTABLE = "/opt/sputnik/pit_diff_tool/sputnik_pit.py";

    private String pitFilter;

    PylintExecutor(@Nullable String pitFilter) {
        this.pitFilter = pitFilter;
    }

    String runOnFile(String filePath) {
        log.info("Review on file: " + filePath);
        return new ExternalProcess().executeCommand(buildParams());
    }

    @NotNull
    private String[] buildParams() {
        List<String> basicPitArgs = ImmutableList.of("python", 
                PIT_EXECUTABLE
                );
        List<String> pitFilterNameArg = getPitFilterAsList();
        List<String> allArgs = Lists.newArrayList(Iterables.concat(basicPitArgs, pitFilterNameArg));
        return allArgs.toArray(new String[allArgs.size()]);
    }

    private List<String> getPitFilterAsList() {
        if (rcfileName == null) {
            return ImmutableList.of();
        }
        return ImmutableList.of(pitFilter);
    }
}
