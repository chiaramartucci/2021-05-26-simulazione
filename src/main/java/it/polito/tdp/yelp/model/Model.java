package it.polito.tdp.yelp.model;

import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;

import it.polito.tdp.yelp.db.YelpDao;

public class Model {
	
	YelpDao dao = new YelpDao();
	private Graph<Business, DefaultWeightedEdge> grafo;
	private List<Business> vertici;
	private Map<String, Business> verticiIdMap = new HashMap<String, Business>();
	
	
	public List<String> getAllCities (){
		return dao.getAllCities();
	}
	
	public String creaGrafo (String city, Year anno) {
		this.grafo = new SimpleDirectedWeightedGraph<Business, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		vertici = dao.getVertici(city, anno);
	
		// popolo idMap
		for(Business b: vertici) {
			verticiIdMap.put(b.getBusinessId(), b);
		}
		// vertici
		Graphs.addAllVertices(this.grafo, this.vertici);
		
		
		// archi
		List<ArcoGrafo> archi = dao.calcolaArchi(city, anno);
		for(ArcoGrafo arco: archi) {
			Graphs.addEdge(this.grafo, this.verticiIdMap.get(arco.getBusinessId1()), this.verticiIdMap.get(arco.getBusinessId2()), arco.getPeso());
		}
		
		return String.format("grafo creato con %d vertici e %d archi", this.grafo.vertexSet().size(), this.grafo.edgeSet().size());
	}
	
	public Business getLocaleMigliore () {
		double max =0.0;
		Business result= null;
		
		for(Business b : this.grafo.vertexSet()) {
			double val = 0.0;
			for(DefaultWeightedEdge e: this.grafo.incomingEdgesOf(b)) {
				val += this.grafo.getEdgeWeight(e);
			}
			for(DefaultWeightedEdge e: this.grafo.outgoingEdgesOf(b)) {
				val -= this.grafo.getEdgeWeight(e);
			}
			
			if(val>max) {
				max=val;
				result=b;
			}
		}
		
		return result;
	}
}
