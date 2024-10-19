package it.unibo.sap.layered.archunit;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.ArchUnitRunner;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.runner.RunWith;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;


@RunWith(ArchUnitRunner.class)
@AnalyzeClasses(packages = "sap.ass01.clean")
public class CleanArchitectureTest {

    private static final String DOMAIN_LAYER = "sap.ass01.clean.domain..";
    private static final String INFRASTRUCTURE_LAYER = "sap.ass01.clean.infrastructure..";

    @ArchTest
    public static final ArchRule cleanArchitectureAccessRule = layeredArchitecture().consideringAllDependencies()
            .layer("Domain").definedBy(DOMAIN_LAYER)
            .layer("Infrastructure").definedBy(INFRASTRUCTURE_LAYER)
            .whereLayer("Infrastructure").mayNotBeAccessedByAnyLayer()
            .whereLayer("Domain").mayOnlyBeAccessedByLayers("Infrastructure")
            .ignoreDependency(Object.class, Object.class);

    @ArchTest
    public static final ArchRule domainLayerDoesntDependsOnLayers =
            classes().that().resideInAPackage(DOMAIN_LAYER)
            .should().onlyDependOnClassesThat()
            .resideInAnyPackage("java.*..", DOMAIN_LAYER,"com.mysql..",
                    "sap.ass01.clean.utils..","io.vertx..");

    @ArchTest
    public static final ArchRule infrastructureLayerDependsOnlyFromBusinessLayer =
            classes().that().resideInAPackage(INFRASTRUCTURE_LAYER)
            .should().onlyDependOnClassesThat()
            .resideInAnyPackage(DOMAIN_LAYER,"java.*..",
                    INFRASTRUCTURE_LAYER,"javax.*..","io.vertx..","sap.ass01.clean.utils..",
                    "io.vertx..");
}
