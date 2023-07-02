# This files contains your custom actions which can be used to run
# custom Python code.
#
# See this guide on how to implement these action:
# https://rasa.com/docs/rasa/custom-actions


# This is a simple example for a custom action which utters "Hello World!"

import json
import numpy as np
import pandas as pd
from time import sleep
from typing import Any, Text, Dict, List
import paho.mqtt.client as mqtt
from rasa_sdk import Action, Tracker
from rasa_sdk.executor import CollectingDispatcher
from rasa_sdk.events import AllSlotsReset



class AskSam(Action):
    intentDict={
        "list_all_known_activities":{
                                    "samTriggeringEvent":"humanQuestion",
                                    "payload":"([type(A,a_ctivity),label(A,Activity_List)],[Activity_List])"
                                    },
        "sub_task_of_given_activity":
                                    {
                                    "samTriggeringEvent":"humanQuestion",
                                    "slot":["activitySlot"],
                                    "payload": '([type(A,a_ctivity),label(A,Activity_Label),like(Activity_Label,"{}"),isSubActivityOf(S,A),label(S,SubActivity_Label),hasPriority(S,X),order_by([X])],[X,SubActivity_Label])'},
        "details_all_known_activities":{
                                    "samTriggeringEvent":"humanQuestion",
                                    "payload":"([type(A,a_ctivity),label(A,Activity_List),comment(A,Description)],[Activity_List,Description])"                             
        },
        "details_specific_activity":{
                                    "samTriggeringEvent":"humanQuestion",
                                    "slot":["activitySlot"],
                                    "payload": '([type(A,a_ctivity),label(A,Activity_Label),like(Activity_Label,"{}"),comment(A,Description)],[Description])'
                                    },



        "precondition_of_sub_task_at_given_step":{
                                    "samTriggeringEvent":"humanQuestion",
                                    "slot":["taskNbrSlot"],
                                    "payload": '([type(S,s_ubActivity),label(S,SubActivity_Label),hasPriority(S,{}),after(S,T),label(T,TODO_before)],[TODO_before])'
        },
        "successor_of_sub_task_at_given_step":{
                                    "samTriggeringEvent":"humanQuestion",
                                    "slot":["taskNbrSlot"],
                                    "payload": '([type(S,s_ubActivity),label(S,SubActivity_Label),hasPriority(S,{}),hasSuccessor(S,T),label(T,TODO_after)],[TODO_after])'
        },
        "successor_of_given_sub_task":{
                                    "samTriggeringEvent":"humanQuestion",
                                    "slot":["subActivitySlot"],
                                    "payload": '([type(S,s_ubActivity),label(S,SubActivity_Label),hasPriority(S,Step),like(S,"{}"),hasSuccessor(S,T),label(T,TODO_after)],[TODO_after])'
        },
        "precondition_of_given_sub_task":{
                                    "samTriggeringEvent":"humanQuestion",
                                    "slot":["subActivitySlot"],
                                    "payload": '([type(S,s_ubActivity),label(S,SubActivity_Label),hasPriority(S,Step),like(S,"{}"),after(S,T),label(T,TODO_before)],[TODO_before])'
        }
        
        # "ask2RecoverConcept":{
        #                             "samTriggeringEvent":"resource",
        #                             "slot":["activitySlot"],
        #                             "payload": '([type(A,a_ctivity),label(A,Activity_Label),like(Activity_Label,"{}"),isSubActivityOf(S,A),label(S,SubActivity_Label),hasPriority(S,X),order_by([X])],[X,SubActivity_Label])'},
        # },
    }
        # "sub_task_of_given_step":,
        # "precondition_of_sub_task_at_given_step":,
        # "successor_of_sub_task_at_given_step":,
        # "precondition_of_given_sub_task":,
        # "successor_of_given_sub_task":,
        
    # samTriggeringEvents={
    #     "ask2UseResource":"+resource",
    #     "askSam":"+humanQuestion",
    #     "ask2Infer":"!infUsing",
    #     "ask2LearnOnto":"?prefix",
    # }

    def __init__(self):
        self.client=mqtt.Client()
        self.pub_topic="rasa_channel"
        self.sub_topic="sam_channel"
        self.broker_address = "localhost"
        self.broker_port = 1883
        self.received_message = ""
        self.dispatcher = None
        self.is_waiting = True

        self._setup_client()

    def _setup_client(self):
        self.client.connect(self.broker_address,self.broker_port, 60)
        self.client.subscribe(self.sub_topic)
        self.client.on_message = self._on_message
        self.client.loop_start()


    def _on_message(self, client, userdata, msg):
        # print(msg.payload)
        self.received_message =json.loads(msg.payload.decode().replace('\n', ''))
        # qui fai handle del risultato ottenuto da java
        self.is_waiting = False
        # self.dispatcher
        # self.dispatcher.utter_message(text=str(self.received_message))

        # self.client.loop_stop()


    def name(self) -> Text:
        return "ask_sam"
    

    def run(self, dispatcher: CollectingDispatcher,
            tracker: Tracker,
            domain: Dict[Text, Any]) -> List[Dict[Text, Any]]:

        self.dispatcher = CollectingDispatcher
        # dispatcher.utter_message(text="Lemme ask Sam")
        intent=tracker.latest_message['intent'].get('name')
        print("VIAO")
        to_send=""
        if intent in self.intentDict.keys():
            if 'slot' in self.intentDict[intent].keys():
                slots=[str(tracker.get_slot( e )) for e in self.intentDict[intent]['slot']]
                if 'None' in slots:
                    AllSlotsReset()
                    return []
                else:
                    to_send=self.intentDict[intent]['samTriggeringEvent']+self.intentDict[intent]['payload'].format(*slots)
                    print(to_send)
                    print("Slot:",slots)
            else:
                to_send=self.intentDict[intent]['samTriggeringEvent']+self.intentDict[intent]['payload']
        # print("[intent]: "+ tracker.latest_message['intent'].get('name')+" => [msg]: ",end="")
        print("Jason:", intent )

        # print(tuple(self.intentDict.keys()))
        # se lo slot 'e settato e l intent 'e subactivitylist
        # print( tracker.get_slot('chosen_activity') )
        # if (tracker.latest_message['intent'].get('name')):
            # =="list_all_known_activities") and not (tracker.get_slot('activitySlot') is None ):
            # to_send=self.intentDict[intent].format(tracker.get_slot('activitySlot'))
        # to_send=self.intentDict[intent]["samTriggeringEvent"]+self.intentDict[intent]["payload"]#.format(self.intentDict[intent]["slot"])
        # print("Stampo query:",to_send)
            # print( tracker.latest_message['entities'])
            # print("Stampo lo slot: ",tracker.get_slot('chosen_activity') )
        if intent.startswith(tuple(self.intentDict.keys())):
            print("Send Jason" )
            # to_send="request([type(A,a_ctivity),label(A,X)],[A,X])"
            #tracker.latest_message['text']
            self.client.publish(self.pub_topic, to_send)
    
            while self.is_waiting:
                sleep(1)

            # qui handle messaggio
            data = self.received_message
            dispatcher.utter_message(text=f"Sam answer:\n")
            
            out = pd.DataFrame(np.array(data['output']).T, columns=data['input'])
            for col in out.columns:
                out[col] = out[col].apply(lambda x: x if len(x)<=50 else x[:46] + ' ...')
            out.columns = [''] * len(out.columns)
            dispatcher.utter_message(text=out.to_string(index=False))
            # for ix,e in enumerate(data['input']):
            #     dispatcher.utter_message(text=f"Sam answer: {e}")
            #     for k in data['output'][ix]:
            #         dispatcher.utter_message(text="\t"+f"{k}")
            self.is_waiting = True

        AllSlotsReset()
        return []
