package it.unibo.sap.layered.archunit;

import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.junit.ArchUnitRunner;
import org.junit.runner.RunWith;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;


@RunWith(ArchUnitRunner.class)
@AnalyzeClasses(packages = "sap.ass01.layers")
public class LayeredArchitectureTest {

    private static final String DAL_LAYER = "sap.ass01.layers.DataAccessL..";
    private static final String BLL_LAYER = "sap.ass01.layers.BusinessLogicL..";
    private static final String PreL_LAYER = "sap.ass01.layers.GUI..";
    private static final String PerL_LAYER = "sap.ass01.layers.PersistenceL..";

    @ArchTest
    public static final ArchRule layeredArchitectureAccessRule = layeredArchitecture().consideringAllDependencies()
            .layer("DB").definedBy(DAL_LAYER)
            .layer("Persistence").definedBy(PerL_LAYER)
            .layer("Logic").definedBy(BLL_LAYER)
            .layer("Presentation").definedBy(PreL_LAYER)
            .whereLayer("Presentation").mayNotBeAccessedByAnyLayer()
            .whereLayer("Logic").mayOnlyBeAccessedByLayers("Presentation")
            .whereLayer("Persistence").mayOnlyBeAccessedByLayers("Logic")
            .whereLayer("DB").mayOnlyBeAccessedByLayers("Persistence")
            .ignoreDependency(java.lang.Object.class, Object.class);

    @ArchTest
    public static final ArchRule businessLayerDependsOnlyFromPersistentLayer =
            classes().that().resideInAPackage(BLL_LAYER)
            .should().onlyDependOnClassesThat()
            .resideInAnyPackage(PerL_LAYER,"java.*..",
                    BLL_LAYER,"sap.ass01.layers.utils..","io.vertx..");

    @ArchTest
    public static final ArchRule persistenceLayerDependsOnlyFromDBLayer =
            classes().that().resideInAPackage(PerL_LAYER)
                    .should().onlyDependOnClassesThat()
                    .resideInAnyPackage(DAL_LAYER,"java.*..",
                            PerL_LAYER,"sap.ass01.layers.utils..","io.vertx..");

    @ArchTest
    public static final ArchRule dataAccessLayerDoesntDependsOnLayers =
            classes().that().resideInAPackage(DAL_LAYER)
            .should().onlyDependOnClassesThat()
            .resideInAnyPackage("java.*..", DAL_LAYER,"com.mysql..",
                    "sap.ass01.layers.utils..");

    @ArchTest
    public static final ArchRule presentationLayerDependsOnlyFromBusinessLayer =
            classes().that().resideInAPackage(PreL_LAYER)
            .should().onlyDependOnClassesThat()
            .resideInAnyPackage(BLL_LAYER,"java.*..",
                    PreL_LAYER,"javax.*..","io.vertx..","sap.ass01.layers.utils..");
}
