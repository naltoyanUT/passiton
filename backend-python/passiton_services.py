__author__ = 'ntoyan'

from google.appengine.ext import ndb
import datetime
import logging


class State:
    OFFERED = 1
    ACCEPTED = 2
    COLLECTED = 3
    CLOSED = 4

class Item(ndb.Model):
    name = ndb.StringProperty()
    image = ndb.BlobProperty()
    description = ndb.StringProperty()
    category = ndb.StringProperty()

    owner = ndb.StringProperty()
    previous_owner = ndb.StringProperty()

    create_date = ndb.DateTimeProperty(auto_now_add=True)
    last_updated = ndb.DateTimeProperty(auto_now_add=True)

    latitude=ndb.FloatProperty()
    longitude=ndb.FloatProperty()

    state = ndb.IntegerProperty()
    visible_to = ndb.StringProperty(repeated=True)



    @staticmethod
    def create_item(name, image, description, category, create_date, lat, lon, owner, visible_to=[]):
        m = Item()
        m.name = name
        m.image = image
        m.description = description
        m.category = category

        m.owner = owner
        m.previous_owner = owner

        #m.create_date = create_date
        #m.last_updated = create_date

        m.latitude=lat
        m.longitude=lon

        m.state = State.OFFERED
        m.visible_to = visible_to

        m.put()

        return m.key



    #business logic

    #Original owner tools
    def delete_item(self, user, current_date):
        if(self.previous_owner == user # the user is the original owner
           and ( self.state == State.OFFERED  or self.state == State.COLLECTED # and it's currently not wanted by anyone or has been collected by a friend
                (self.state == State.ACCEPTED and current_date - self.last_updated > datetime.timedelta(days=7))) ): #or wanted by a friend for more than 7 days
            return self.change_state(State.CLOSED, user, current_date)
        else: return -1


    #Friend tools
    def want_item(self, user, current_date):
        if(self.owner != user and self.state == State.OFFERED):
            #logging.error("user = " + user + ", owner = " + self.owner + ", state = " + str(self.state))
            return self.change_state(State.ACCEPTED, user, current_date)

    def cancel_item(self, user, current_date): #A friend wishes to cancel collecting an item
        if(self.owner == user and self.previous_owner != user and self.state == State.ACCEPTED):
            return self.change_state(State.OFFERED, self.previous_owner, current_date)


    def collect_item(self, user, current_date):
        if(self.owner == user and self.previous_owner != user  and self.state == State.ACCEPTED):
            return self.change_state(State.COLLECTED, user, current_date)


    def change_state(self, new_state, new_user, new_date):
        if(new_state == State.CLOSED):
            self.key.delete()
            return 0
        else:
            self.owner = new_user
            self.state = new_state
            self.last_updated = new_date
            self.put()
            return 0

    @staticmethod
    def get_item(key):
        item_key = ndb.Key(urlsafe=key)
        return item_key.get()

    @staticmethod
    def get_item_image(key):
        item_key = ndb.Key(urlsafe=key)
        return item_key.get().image

    #Offers View
    #Items offered by others and are currently available
    @staticmethod
    def get_offers(user_id, category):
        if category == 'all':
            return Item.query(ndb.AND(Item.owner != user_id,
                                  Item.state == State.OFFERED),
                                  Item.visible_to.IN([user_id])).fetch()
        else:
            return Item.query(ndb.AND(Item.owner != user_id,
                                      Item.state == State.OFFERED,
                                      Item.category == category),
                                      Item.visible_to.IN([user_id])).fetch()


    #MyList View
    @staticmethod
    def get_my_offers(user_id):
        return Item.query(ndb.AND(Item.owner == user_id,
                                  Item.state == State.OFFERED)).fetch()


    @staticmethod
    def get_my_wanted(user_id):
        return Item.query(ndb.AND(Item.owner == user_id,
                                  Item.state == State.ACCEPTED)).fetch()


    @staticmethod
    def get_on_hold(user_id):
        return Item.query(ndb.AND(Item.previous_owner == user_id,
                                  Item.state == State.ACCEPTED)).fetch()

    #def want_it(self):