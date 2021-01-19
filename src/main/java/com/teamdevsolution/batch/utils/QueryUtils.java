package com.teamdevsolution.batch.utils;

public interface QueryUtils {

    String FORMATEUR_INSERT_QUERY="INSERT INTO formateurs (id,nom,prenom,adresse_email) VALUES(?,?,?,?)";
    String FORMATION_INSERT_QUERY="INSERT INTO formations (code,libelle,descriptif) VALUES(?,?,?)";
    String SEANCE_INSERT_QUERY="INSERT INTO seances (code_formation,id_formateur,date_debut,date_fin) VALUES(?,?,?,?)";
    String SELECT_FORMATEUR_QUERY="select distinct f.* from formateurs f join seances s on f.id=s.id_formateur";

    String SELECT_SEANCE_QUERY = "select f.libelle, s.date_debut,s.date_fin"
            + " from formations f join seances s on f.code=s.code_formation"
            + " where s.id_formateur=:formateur"
            + " order by s.date_debut ";

    String COUNT_SEANCE_QUERY = "select count(*) from seances";
}
