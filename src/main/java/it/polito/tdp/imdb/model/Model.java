package it.polito.tdp.imdb.model;

import java.time.Year;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.imdb.db.ImdbDAO;

public class Model {

	private Graph<Actor,DefaultWeightedEdge>grafo;
	private List<Actor> actors;
	private Map<Integer, Actor> IdMap;
	private ImdbDAO dao;
	
	public Model() {
		this.dao = new ImdbDAO();
	}
	
	public void CreaGrafo(String genere) {
		
		this.actors = this.dao.getActors(genere);
		this.IdMap = new HashMap<>();
		for (Actor a : this.actors)
			this.IdMap.put(a.getId(), a);

		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);

		Graphs.addAllVertices(this.grafo, this.actors);
		
		List<Interactions> lista=this.dao.getArchi(genere, IdMap);
		
		for(Interactions i: lista) {
			Graphs.addEdge(this.grafo, i.getA1(), i.getA2(), i.getPeso());
		}
	}

	public List<String> getGeneri() {

		return this.dao.GetAllGeneri();
	}

	public Object getNumArchi() {
		return this.grafo.edgeSet().size();
		
	}

	public Object getNumVertici() {
		return this.grafo.vertexSet().size();
	}
	
	public List<Actor> getActors() {
		List<Actor> actors = new ArrayList<>(grafo.vertexSet());
		Collections.sort(actors, new Comparator<Actor>() {

			@Override
			public int compare(Actor o1, Actor o2) {
				return o1.lastName.compareTo(o2.lastName);
			}
			
		});
		return actors;
	}
	
	public List<Actor> getConnectedActors(Actor a){
		ConnectivityInspector<Actor, DefaultWeightedEdge> ci = new ConnectivityInspector<Actor, DefaultWeightedEdge>(this.grafo);
		List<Actor> actorss = new ArrayList<>(ci.connectedSetOf(a));
		actorss.remove(a);
		Collections.sort(actorss, new Comparator<Actor>(){

			@Override
			public int compare(Actor o1, Actor o2) {
				return o1.lastName.compareTo(o2.lastName);
			}
			
		});
		return actorss;
	}
}
