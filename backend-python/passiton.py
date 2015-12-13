import json
from google.appengine.ext import ndb
from passiton_services import Item
import datetime
import webapp2

class GetOffers(webapp2.RequestHandler):
    def get(self):
        user_id = self.request.get('user_id')
        category = self.request.get('category', 'all')
        offers_list = Item.get_offers(user_id, category)
        if(len(offers_list)>1):
            offers_list = sorted(offers_list, key=lambda x: x.last_updated, reverse=True)
        result = []
        for item in offers_list:
            result.append({"key":item.key.urlsafe(),'name':item.name, 'owner':item.owner})

        self.response.write(json.dumps({'result' : result}, sort_keys=True,indent=4, separators=(',', ': ')))


class MyList(webapp2.RequestHandler):
    def get(self):
        user_id = self.request.get('user_id')
        offered = Item.get_my_offers(user_id)
        wanted = Item.get_my_wanted(user_id)
        pending = Item.get_on_hold(user_id)
        offered_list = []
        wanted_list = []
        pending_list = []

        for item in offered:
            offered_list.append({"key":item.key.urlsafe(),'name':item.name, 'owner':item.owner})

        for item in wanted:
            wanted_list.append({"key":item.key.urlsafe(),'name':item.name, 'owner':item.owner})

        for item in pending:
            pending_list.append({"key":item.key.urlsafe(),'name':item.name, 'owner':item.owner})

        self.response.write(json.dumps({'offered' : offered_list, 'wanted':wanted_list, 'pending':pending_list}, sort_keys=True,indent=4, separators=(',', ': ')))


class ItemDetails(webapp2.RequestHandler):
    def get(self):
        item_key = self.request.get('key')
        item = Item.get_item(item_key)
        if(item):
            self.response.write(json.dumps(
                {'key' : item.key.urlsafe(), 'state':str(item.state), 'name':item.name, 'create_date':str(item.create_date),
                 'last_update':str(item.last_updated), 'owner':item.owner, 'previous_owner':item.previous_owner,
                 'lat':str(item.latitude), 'lon':str(item.longitude), 'description':item.description},
                sort_keys=True,indent=4, separators=(',', ': ')))
        else: self.response.write("no matching item")

class WriteImage(webapp2.RequestHandler):
    def get(self):
        image = Item.get_item_image(self.request.get('key'))
        if image:
            self.response.headers['Content-Type'] = 'image/png'
            self.response.out.write(image)
        else:
            self.response.out.write('No image')

class CreateItem(webapp2.RequestHandler):
    def post(self):
        user_id = self.request.get('user_id', 'empty')

        #item fields - required
        name = self.request.get("name")
        image = self.request.get('file')
        category = self.request.get("category", 'all')
        create_date = self.request.get("create_date")
        friends = self.request.get("friends")
        visible_to = [x.strip() for x in friends.split(',')]


        #item fields - optional
        description = self.request.get("description")
        latitude = self.request.get("latitude")
        longitude = self.request.get("longitude")
        if(latitude == ""):latitude = 0
        if(longitude == ""): longitude = 0


        item_key = Item.create_item(name, image, description,category,create_date,float(latitude),float(longitude),user_id, visible_to)

        self.redirect('/details?key='+item_key.urlsafe())
        #self.response.write(json.dumps({'user_id' : user_id, 'offered':offers, 'want':['item c']}, sort_keys=True,indent=4, separators=(',', ': ')))

class DeleteItem(webapp2.RequestHandler):
    def post(self):
        user_id = self.request.get('user_id')
        current_date = datetime.datetime.today() #self.request.get('current_date')
        item_key = self.request.get('key')

        item = Item.get_item(item_key)
        response = "";
        if(item):
            result = item.delete_item(user_id, current_date)
            if(result == 0):
                response = "success"
            elif(result == -1):
                response = "condition not satisfied"
            else: response = "cannot be deleted"
        else: response = "not item found"
        self.response.write(json.dumps({"result":response}))


class WantItem(webapp2.RequestHandler):
    def post(self):
        user_id = self.request.get('user_id', 'empty')
        current_date = datetime.datetime.today() #self.request.get('current_date')
        item_key = self.request.get('key')
        item = Item.get_item(item_key)
        response = ""
        if(item):
            result = item.want_item(user_id, current_date)
            if(result == 0):
                response = "success"
            else: response = "unable to reserve"
        else: response = "no item found"
        self.response.write(json.dumps({'result': response}))


class CancelItem(webapp2.RequestHandler):
    def post(self):
        user_id = self.request.get('user_id', 'empty')
        current_date = datetime.datetime.today() #self.request.get('current_date')
        item_key = self.request.get('key')

        item = Item.get_item(item_key)
        response = ""
        if(item):
            result = item.cancel_item(user_id, current_date)
            if(result == 0): response = "success"
            else: response = "cannot be cancelled"
        else: response = "not item found"
        self.response.write(json.dumps({'result': response}))

class CollectItem(webapp2.RequestHandler):
    def post(self):
        user_id = self.request.get('user_id', 'empty')
        current_date = datetime.datetime.today() #self.request.get('current_date')
        item_key = self.request.get('key')

        item = Item.get_item(item_key)
        response = ""
        if(item):
            result = item.collect_item(user_id, current_date)
            if(result == 0): response="success"
            else: response="cannot be collected"
        else: response="not item found"
        self.response.write(json.dumps({'result': response}))

app = webapp2.WSGIApplication([
    ('/offers', GetOffers),
    ('/mylist', MyList),
    ('/new', CreateItem),
    ('/details', ItemDetails),
    ('/image', WriteImage),
    ('/delete', DeleteItem),
    ('/cancel', CancelItem),
    ('/want', WantItem),
    ('/collect', CollectItem)

], debug=True)