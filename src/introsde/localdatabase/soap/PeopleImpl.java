package introsde.localdatabase.soap;

import introsde.localdatabase.model.Measure;
import introsde.localdatabase.model.Person;
import introsde.localdatabase.model.Activity;

import java.text.ParseException;
import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebService;

//Service Implementation

@WebService(endpointInterface = "introsde.localdatabase.soap.People",
	serviceName="PeopleService")
public class PeopleImpl implements People {

	@Override
	public Person readPerson(Long id) {
		System.out.println("---> Reading Person by id = "+id);
		Person p = Person.getPersonById(id);
		if (p!=null) {
			System.out.println("---> Found Person by id = "+id+" => "+p.getFirstname());
			System.out.println("current");
		} else {
			System.out.println("---> Didn't find any Person with  id = "+id);
		}
		return p;
	}

	@Override
	public List<Person> getPeople() {
		List<Person> personList = Person.getAll();
		return personList;
	}

	@Override
	public Person addPerson(Person person) {
		Person.savePerson(person);
		return person;
	}

	@Override
	public Person updatePerson(Person person) {
		person.setHealthHistory(Measure.getAll());
		person.setCurrentHealth(Measure.getCurrentMeasuresById(person.getIdPerson()));
        Person existing = Person.getPersonById(person.getIdPerson());

        if (existing == null) {
            return null;
        } else {
            //take the non specified fields from the db
            if (person.getFirstname() == null) {
                person.setFirstname(existing.getFirstname());
            }
            if (person.getLastname() == null) {
                person.setLastname(existing.getLastname());
            }
            if (person.getBirthdate() == null) {
                try {
                    person.setBirthdate(existing.getBirthdate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (person.getEmail() == null) {
                person.setEmail(existing.getEmail());
            }
            if (person.getAuthSecret() == null) {
                person.setAuthSecret(existing.getAuthSecret());
            }
            if (person.getAuthToken() == null) {
                person.setAuthToken(existing.getAuthToken());
            }
            if (person.getChatId() == null) {
                person.setChatId(existing.getChatId());
            }
            if (person.getCaloriesMeal() == null) {
                person.setCaloriesMeal(existing.getCaloriesMeal());
            }
        }         
		Person updatedPerson = Person.updatePerson(person);
		return updatedPerson;
	}

	@Override
	public int deletePerson(Long id) {
		Person p = Person.getPersonById(id);
		if (p!=null) {
			Person.removePerson(p);
			return 0;
		} else {
			return -1;
		}
	}
	
	@Override
	public Person getPersonById(Long personId) {
		return Person.getPersonById(personId);
	}

	@Override
	public List<Measure> readPersonHistory(Long id, String measureType) {
		return Measure.getMeasureByIdAndType(id, measureType);
	}

	@Override
	public List<Measure> readMeasureTypes() {
		return Measure.getAll();
	}

	@Override
	public Measure readPersonMeasure(Long id, String measureType, Long mid) {
		return Measure.getMeasureByIdTypeAndMid(id, measureType, mid);
	}

	@Override
	public Measure savePersonMeasure(Long id, Measure measure) {
		measure.setPerson(Person.getPersonById(id));
		return Measure.saveMeasure(measure);
	}

	@Override
	public Measure updatePersonMeasure(Long id, Measure measure) {
		Measure dbMeasure = Measure.getMeasureById(measure.getIdMeasure());
		if (dbMeasure.getPerson().getIdPerson() == id) {
			measure.setPerson(Person.getPersonById(id));
			return Measure.updateMeasure(measure);
		} else {
			return null;
		}
	}
	
	@Override
	public List<Activity> readActivity(Long id) {
		return Activity.getPersonActivity(id);
	}
	
	@Override
	public Activity createActivity(Long id, Activity a) {
		a.setPerson(Person.getPersonById(id));
		return Activity.saveActivity(a);
	}
	
	@Override
	public Activity updateActivity(Long id, Activity a) {
		Activity activity = a.getActivityById(a.getIdActivity());
		if (activity.getPerson().getIdPerson() == id) {
			a.setPerson(Person.getPersonById(id));
			return Activity.updateActivity(a);
			} else {
				return null;
			}
	}
	
	@Override 
	public int deleteActivity(Long id) {
		Activity a = Activity.getActivityById(id);
		
		if (a!=null) {
			Activity.removeActivity(a);
			return 0;
		} else {
			return -1;
		}
	}
}
