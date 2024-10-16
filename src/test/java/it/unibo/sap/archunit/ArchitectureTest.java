package it.unibo.sap.archunit;

import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.ArchUnitRunner;
import org.junit.runner.RunWith;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;


@RunWith(ArchUnitRunner.class)
@AnalyzeClasses(packages = "sap.ass01.layers")
public class ArchitectureTest {

    @ArchTest
    public static final ArchRule layeredArchitectureAccessRule = layeredArchitecture().consideringAllDependencies()
            .layer("Persistence").definedBy("sap.ass01.layers.DAL..")
            .layer("Logic").definedBy("sap.ass01.layers.BLL..")
            .layer("Presentation").definedBy("sap.ass01.layers.PL..")
            .whereLayer("Presentation").mayNotBeAccessedByAnyLayer()
            .whereLayer("Logic").mayOnlyBeAccessedByLayers("Presentation")
            .whereLayer("Persistence").mayOnlyBeAccessedByLayers("Logic")
            .ignoreDependency(java.lang.Object.class, Object.class);

    @ArchTest
    public static final ArchRule businessLayerDependsOnlyFromDataAccessLayer =
            classes().that().resideInAPackage("sap.ass01.layers.BLL..")
            .should().onlyDependOnClassesThat()
            .resideInAnyPackage("sap.ass01.layers.DAL..","java.*..",
                    "sap.ass01.layers.BLL..","sap.ass01.layers.utils..","io.vertx..");

    @ArchTest
    public static final ArchRule dataAccessLayerDoesntDependsOnLayers =
            classes().that().resideInAPackage("sap.ass01.layers.DAL..")
            .should().onlyDependOnClassesThat()
            .resideInAnyPackage("java.*..","sap.ass01.layers.DAL..","com.mysql..",
                    "sap.ass01.layers.utils..");

    @ArchTest
    public static final ArchRule presentationLayerDependsOnlyFromBusinessLayer =
            classes().that().resideInAPackage("sap.ass01.layers.PL..")
            .should().onlyDependOnClassesThat()
            .resideInAnyPackage("sap.ass01.layers.BLL..","java.*..",
                    "sap.ass01.layers.PL..","javax.*..","io.vertx..","sap.ass01.layers.utils..");
}
