rootProject.name = "Awale"
include("Server","Client") //Définir des sous-projets au sein d'un seul build
includeBuild("Shared") //Peut être dans des dépôts séparés