package it.unibo.sap.archunit;

import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.ArchUnitRunner;
import org.junit.runner.RunWith;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;


@RunWith(ArchUnitRunner.class)
@AnalyzeClasses(packages = "sap.ass01.layers")
public class ArchitectureTest {

    @ArchTest
    public static final ArchRule rule = layeredArchitecture().consideringAllDependencies()

            .layer("Persistence").definedBy("sap.ass01.layers.DAL..")
            .layer("Logic").definedBy("sap.ass01.layers.BLL..")
            .layer("Presentation").definedBy("sap.ass01.layers.PL..")

            .whereLayer("Presentation").mayNotBeAccessedByAnyLayer()
            .whereLayer("Logic").mayNotBeAccessedByAnyLayer()
            .whereLayer("Persistence").mayOnlyBeAccessedByLayers("Logic")
            .ignoreDependency(java.lang.Object.class, Object.class);
}
