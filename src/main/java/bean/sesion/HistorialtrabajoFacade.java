/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bean.sesion;

import entidades.Historialtrabajo;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Saul
 */
@Stateless
public class HistorialtrabajoFacade extends AbstractFacade<Historialtrabajo> {

    @PersistenceContext(unitName = "com.mycompany_Proveedoressw_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public HistorialtrabajoFacade() {
        super(Historialtrabajo.class);
    }
    
}
