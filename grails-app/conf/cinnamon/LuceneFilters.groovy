package cinnamon

import cinnamon.index.IndexAction
import cinnamon.index.Indexable

class LuceneFilters {

    def luceneService
    def userService
    
    def filters = {
        all(controller: '*', action: '*') {
            before = {

            }
            after = { Map model ->
                
            }
            afterView = { Exception e ->
                if(e){    
                    log.debug("*** After View-Exception ***",e)
                }
                if(session.repositoryName && e == null){
                    // only act if the user is logged in.
                    ObjectSystemData.withNewTransaction {                        
                        Map<Indexable, IndexAction> updatedObjects = LocalRepository.getUpdatedObjects();
                        for(Indexable indexable : updatedObjects.keySet()){
                            log.debug("Working on indexable #"+indexable.myId());
                            switch(  updatedObjects.get(indexable)){
                                case IndexAction.ADD: luceneService.addToIndex(indexable);break;
                                case IndexAction.UPDATE: luceneService.updateIndex(indexable);break;
                                case IndexAction.REMOVE : luceneService.removeFromIndex(indexable);break;
                            }
                        }
                    }
                }
                LocalRepository.cleanUp()
            }
        }
    }
}
