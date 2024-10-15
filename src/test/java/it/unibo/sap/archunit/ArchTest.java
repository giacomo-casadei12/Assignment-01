package it.unibo.sap.archunit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

public class ArchTest {

    @Test
    public void businessLayerDependsOnlyFromDataAccessLayer() {
        JavaClasses importedClasses = new ClassFileImporter().importPackages("sap.ass01.layers.BLL");

        ArchRule rule = classes().should().dependOnClassesThat().resideInAPackage("sap.ass01.layers.DAL")
                .orShould().dependOnClassesThat().resideInAPackage("java.*");

        rule.check(importedClasses);
    }

    @Test
    public void dataAccessLayerDoesntDependsOnLayers() {
        JavaClasses importedClasses = new ClassFileImporter().importPackages("sap.ass01.layers.DAL");

        ArchRule rule = classes().should().dependOnClassesThat().resideInAPackage("java.*");

        rule.check(importedClasses);
    }

    @Test
    public void presentationLayerDependsOnlyFromBusinessLayer() {
        JavaClasses importedClasses = new ClassFileImporter().importPackages("sap.ass01.layers.PL");

        ArchRule rule = classes().should().dependOnClassesThat().resideInAPackage("sap.ass01.layers.BLL")
                .orShould().dependOnClassesThat().resideInAPackage("java.*");

        rule.check(importedClasses);
    }

}
